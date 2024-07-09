package com.guyuuan.app.find_author.ui.screen.bucket

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.guyuuan.app.find_author.core.data.MediaRepository
import com.guyuuan.app.find_author.core.data.model.BucketItem
import com.guyuuan.app.find_author.core.ui.BaseViewModel
import com.guyuuan.app.find_author.core.ui.UiEvent
import com.guyuuan.app.find_author.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 6/11/24 17:03
 * @description:
 **/
@HiltViewModel
class ChooseBucketsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : BaseViewModel<ChooseBucketsUiState, ChooseBucketsEvent>() {
    private val pager = mediaRepository.getPagingBuckets().flow.cachedIn(viewModelScope)
    override val uiState: StateFlow<ChooseBucketsUiState> = flow<ChooseBucketsUiState> {
        emit(ChooseBucketsUiState.Success(pager))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ChooseBucketsUiState.Loading
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepository.loadSystemBuckets()
        }

    }

    override suspend fun onEvent(event: ChooseBucketsEvent) {
        when (event) {
            is ChooseBucketsEvent.SelectBucket -> {
                mediaRepository.updateBucket(event.bucket)
            }
        }
    }

}

sealed interface ChooseBucketsUiState : UiState {
    data object Loading : ChooseBucketsUiState
    data class Success(val buckets: Flow<PagingData<BucketItem>>) : ChooseBucketsUiState
    data class Error(val error: Throwable) : ChooseBucketsUiState
}

sealed interface ChooseBucketsEvent : UiEvent {
    data class SelectBucket(val bucket: BucketItem) : ChooseBucketsEvent
}