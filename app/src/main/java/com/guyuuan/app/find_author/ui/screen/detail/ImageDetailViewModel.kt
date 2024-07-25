package com.guyuuan.app.find_author.ui.screen.detail

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.guyuuan.app.find_author.core.data.MediaRepository
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.domain.ShareImageUseCase
import com.guyuuan.app.find_author.core.ui.BaseViewModel
import com.guyuuan.app.find_author.core.ui.UiEvent
import com.guyuuan.app.find_author.core.ui.UiState
import com.ramcosta.composedestinations.generated.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * @author: guyuuan
 * @createTime: 7/23/24 09:44
 * @description:
 **/
@HiltViewModel
class ImageDetailViewModel @Inject constructor(
    mediaRepository: MediaRepository, savedStateHandle: SavedStateHandle,
    private val shareImageUseCase: ShareImageUseCase
) : BaseViewModel<ImageDetailUiState, ImageDetailUiEvent>() {
    private val pager = mediaRepository.getImages()
    override val uiState: StateFlow<ImageDetailUiState>
        get() = _uiState
    private val _uiState =
        MutableStateFlow(ImageDetailUiState.Success(pager, savedStateHandle.navArgs()))


    override suspend fun onEvent(event: ImageDetailUiEvent) {
        when (event) {
            is ImageDetailUiEvent.OnPageChange -> {
                when (val state = uiState.value) {
                    is ImageDetailUiState.Success -> {
                        _uiState.emit(state.copy(currentImage = event.currentImage))
                    }
                }
            }

            is ImageDetailUiEvent.OnClickShare -> {
                when (val state = uiState.value) {
                    is ImageDetailUiState.Success -> {
                        state.currentImage?.let { i ->
                            shareImageUseCase(i, event.context)
                        }
                    }
                }
            }
        }
    }
}

interface ImageDetailUiState : UiState {
    data object Loading : ImageDetailUiState

    @Immutable
    data class Success(
        val images: Flow<PagingData<ImageItem>>, val navArgs: ImageDetailScreenNavArgs,
        val currentImage: ImageItem? = null
    ) : ImageDetailUiState

    @Immutable
    data class Error(val throwable: Throwable) : ImageDetailUiState
}

interface ImageDetailUiEvent : UiEvent {
    data class OnPageChange(val currentImage: ImageItem) : ImageDetailUiEvent
    data class OnClickShare(val context: Context) : ImageDetailUiEvent
}



