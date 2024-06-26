package com.guyuuan.app.find_author.ui.screen.bucket

import androidx.lifecycle.viewModelScope
import com.guyuuan.app.find_author.core.data.MediaRepository
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
) : BaseViewModel<ChooseBucketsUiState, SelectBucketsEvent>() {
    override val uiStat: StateFlow<ChooseBucketsUiState> = mediaRepository.getAllBuckets()
        .map<List<BucketItem>, ChooseBucketsUiState> { ChooseBucketsUiState.Success(it) }
        .catch { emit(ChooseBucketsUiState.Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChooseBucketsUiState.Loading)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepository.loadSystemBuckets()
        }
    }

    override suspend fun onEvent(event: SelectBucketsEvent) {
    }

}

sealed interface ChooseBucketsUiState: UiState {
    data object Loading : ChooseBucketsUiState
    data class Success(val buckets: List<BucketItem>) : ChooseBucketsUiState
    data class Error(val error: Throwable) : ChooseBucketsUiState
}

sealed interface SelectBucketsEvent:UiEvent {

}