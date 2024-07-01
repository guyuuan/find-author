package com.guyuuan.app.find_author.ui.screen.bucket

import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.guyuuan.app.find_author.core.data.media.MediaScanner
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.ui.BaseViewModel
import com.guyuuan.app.find_author.core.ui.UiEvent
import com.guyuuan.app.find_author.core.ui.UiState
import com.ramcosta.composedestinations.generated.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 7/1/24 15:53
 * @description:
 **/
@HiltViewModel
class SAFPreviewViewModel @Inject constructor(
    mediaScanner: MediaScanner, savedStateHandle: SavedStateHandle
) : BaseViewModel<SAFPreviewUIState, SAFPreviewUIEvent>() {
    override val uiStat: StateFlow<SAFPreviewUIState> =
        mediaScanner.scanSAFBucketsImages(savedStateHandle.navArgs<SAFPreviewArgs>().uri.toUri())
            .runningFold(emptyList<ImageItem>()) { list, item ->
                list + item
            }.map<List<ImageItem>, SAFPreviewUIState> {
                SAFPreviewUIState.Success(it)
            }.catch {
                emit(SAFPreviewUIState.Error(it))
            }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                SAFPreviewUIState.Loading
            )

    //
    override suspend fun onEvent(event: SAFPreviewUIEvent) {
        TODO("Not yet implemented")
    }
}

sealed interface SAFPreviewUIState : UiState {
    data object Loading : SAFPreviewUIState
    data class Success(val images: List<ImageItem>) : SAFPreviewUIState
    data class Error(val throwable: Throwable) : SAFPreviewUIState

}

sealed interface SAFPreviewUIEvent : UiEvent