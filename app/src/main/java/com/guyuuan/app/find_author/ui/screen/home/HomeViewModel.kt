package com.guyuuan.app.find_author.ui.screen.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.guyuuan.app.find_author.core.data.MediaRepository
import com.guyuuan.app.find_author.core.data.media.ScanStatus
import com.guyuuan.app.find_author.core.data.model.BucketItem
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.ui.BaseViewModel
import com.guyuuan.app.find_author.core.ui.UiEvent
import com.guyuuan.app.find_author.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 7/5/24 10:26
 * @description:
 **/
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
) : BaseViewModel<HomeUiState, HomeUiEvent>() {
    private val pagingData = mediaRepository.getHomeImages().cachedIn(viewModelScope)
    override val uiState: StateFlow<HomeUiState> =
        mediaRepository.loadBucketsImages().map<ScanStatus<BucketItem>, HomeUiState> { scanStatus ->
                HomeUiState.Success(
                    images = pagingData, scanStatus = scanStatus
                )
            }.catch {
                emit(HomeUiState.Error(it))
            }.stateIn(
                viewModelScope, started = SharingStarted.Lazily, HomeUiState.Loading
            )

    override suspend fun onEvent(event: HomeUiEvent) {
    }
}

sealed interface HomeUiState : UiState {
    data object Loading : HomeUiState
    data class Success(
        val images: Flow<PagingData<ImageItem>>, val scanStatus: ScanStatus<BucketItem>
    ) : HomeUiState

    data class Error(val error: Throwable) : HomeUiState
}

sealed interface HomeUiEvent : UiEvent