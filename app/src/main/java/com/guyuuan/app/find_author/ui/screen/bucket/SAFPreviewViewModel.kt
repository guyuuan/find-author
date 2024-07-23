package com.guyuuan.app.find_author.ui.screen.bucket

import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.guyuuan.app.find_author.core.data.MediaRepository
import com.guyuuan.app.find_author.core.data.media.MediaScanner
import com.guyuuan.app.find_author.core.data.media.ScanStatus
import com.guyuuan.app.find_author.core.data.model.BucketItem
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.ui.BaseViewModel
import com.guyuuan.app.find_author.core.ui.UiEvent
import com.guyuuan.app.find_author.core.ui.UiEventOnFailed
import com.guyuuan.app.find_author.core.ui.UiEventOnSuccess
import com.guyuuan.app.find_author.core.ui.UiState
import com.ramcosta.composedestinations.generated.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author: guyuuan
 * @createTime: 7/1/24 15:53
 * @description:
 **/
@HiltViewModel
class SAFPreviewViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val mediaScanner: MediaScanner,
    private val mediaRepository: MediaRepository
) : BaseViewModel<SAFPreviewUIState, SAFPreviewUIEvent>() {
    override val uiState: StateFlow<SAFPreviewUIState>
        get() = _uiState
    private val _uiState = MutableStateFlow<SAFPreviewUIState>(SAFPreviewUIState.Loading)

    private val navArgs: SAFPreviewArgs get() = savedStateHandle.navArgs()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            mediaScanner.saveSAFPermission(navArgs.uri.toUri())
            val bucket = mediaScanner.getSAFBucketInfo(navArgs.uri.toUri())
            mediaScanner.scanSAFBucketsImages(navArgs.uri.toUri()).collect {
                val success =
                    (_uiState.value as? SAFPreviewUIState.Success) ?: SAFPreviewUIState.Success(
                        bucket, emptyList(), true
                    )
                when (it) {
                    is ScanStatus.Running -> _uiState.value =
                        success.copy(images = (success.images + it.data).sortedByDescending { img -> img.dateAdded })

                    is ScanStatus.Done -> _uiState.value = success.copy(running = false)

                     else -> {

                    }
                }
            }
        }
    }

    override suspend fun onEvent(event: SAFPreviewUIEvent) {
        when (event) {
            is SAFPreviewUIEvent.Confirm -> {
                val state = uiState.value
                if (state is SAFPreviewUIState.Success) {
                    val cover = state.images.firstOrNull()
                    mediaRepository.addBucket(
                        state.bucket.copy(
                            selected = true,
                            coverUri = cover?.uri
                        )
                    )
                }
            }

            is SAFPreviewUIEvent.Cancel -> {
                mediaScanner.removeSAFPermission(navArgs.uri.toUri())
            }
        }
    }
}

sealed interface SAFPreviewUIState : UiState {
    data object Loading : SAFPreviewUIState
    data class Success(val bucket: BucketItem, val images: List<ImageItem>, val running: Boolean) :
        SAFPreviewUIState

    data class Error(val throwable: Throwable) : SAFPreviewUIState

}

sealed interface SAFPreviewUIEvent : UiEvent {
    data class Confirm(
        override val onSuccess: UiEventOnSuccess, override val onFailed: UiEventOnFailed? = null
    ) : SAFPreviewUIEvent
    data class Cancel(override val onSuccess: UiEventOnSuccess):SAFPreviewUIEvent
}