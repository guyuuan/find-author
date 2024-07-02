package com.guyuuan.app.find_author.ui.screen.bucket

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.guyuuan.app.find_author.core.data.MediaRepository
import com.guyuuan.app.find_author.core.data.extension.toBucketItem
import com.guyuuan.app.find_author.core.data.model.BucketItem
import com.guyuuan.app.find_author.core.ui.BaseViewModel
import com.guyuuan.app.find_author.core.ui.UiEvent
import com.guyuuan.app.find_author.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
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
    override val uiState: StateFlow<ChooseBucketsUiState> = Pager(
        config = PagingConfig(
            pageSize = 10
        ),
        pagingSourceFactory = mediaRepository::getPagingBuckets
    ).flow.map { pagingData ->
        pagingData.map { it.toBucketItem() }
    }.map<PagingData<BucketItem>, ChooseBucketsUiState> { ChooseBucketsUiState.Success(it) }
        .catch { emit(ChooseBucketsUiState.Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChooseBucketsUiState.Loading)

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
    data class Success(val buckets: PagingData<BucketItem>) : ChooseBucketsUiState
    data class Error(val error: Throwable) : ChooseBucketsUiState
}

sealed interface ChooseBucketsEvent : UiEvent {
    data class SelectBucket(val bucket: BucketItem) : ChooseBucketsEvent
}