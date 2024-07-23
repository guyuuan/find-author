package com.guyuuan.app.find_author.ui.screen.detail

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.guyuuan.app.find_author.core.data.MediaRepository
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.ui.BaseViewModel
import com.guyuuan.app.find_author.core.ui.UiEvent
import com.guyuuan.app.find_author.core.ui.UiState
import com.ramcosta.composedestinations.generated.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * @author: chunjinchen
 * @createTime: 7/23/24 09:44
 * @description:
 **/
@HiltViewModel
class ImageDetailViewModel @Inject constructor(
    mediaRepository: MediaRepository, savedStateHandle: SavedStateHandle
) : BaseViewModel<ImageDetailUiState, ImageDetailUiEvent>() {
    private val pager = mediaRepository.getImages()
    override val uiState: StateFlow<ImageDetailUiState> =
        flow<ImageDetailUiState> {}.catch { emit(ImageDetailUiState.Error(it)) }.stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                ImageDetailUiState.Success(pager, savedStateHandle.navArgs())
            )

    override suspend fun onEvent(event: ImageDetailUiEvent) {
    }
}

interface ImageDetailUiState : UiState {
    data object Loading : ImageDetailUiState

    @Immutable
    data class Success(
        val images: Flow<PagingData<ImageItem>>, val navArgs: ImageDetailScreenNavArgs
    ) : ImageDetailUiState

    @Immutable
    data class Error(val throwable: Throwable) : ImageDetailUiState
}

interface ImageDetailUiEvent : UiEvent



