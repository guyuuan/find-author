package com.guyuuan.app.find_author.ui.screen.welcome

import androidx.lifecycle.viewModelScope
import com.guyuuan.app.find_author.core.data.AppConfigRepository
import com.guyuuan.app.find_author.core.data.MediaRepository
import com.guyuuan.app.find_author.core.data.model.BucketItem
import com.guyuuan.app.find_author.core.ui.BaseViewModel
import com.guyuuan.app.find_author.core.ui.UiEvent
import com.guyuuan.app.find_author.core.ui.UiEventOnFailed
import com.guyuuan.app.find_author.core.ui.UiEventOnSuccess
import com.guyuuan.app.find_author.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 6/24/24 14:20
 * @description:
 **/
@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
    private val mediaRepository: MediaRepository
) : BaseViewModel<WelcomeUiState, WelcomeEvent>() {
    override val uiState: StateFlow<WelcomeUiState> =
        combine<Boolean, List<BucketItem>, WelcomeUiState>(
            appConfigRepository.getInitializedFlow(), mediaRepository.getSelectedBuckets()
        ) { initialized, buckets ->
            WelcomeUiState.Success(initialized, buckets)
        }.catch {
            emit(WelcomeUiState.Error(it))
        }.stateIn(viewModelScope, SharingStarted.Eagerly, WelcomeUiState.Loading)

    override suspend fun onEvent(event: WelcomeEvent) {
        when (event) {
            is WelcomeEvent.Confirm -> {
                appConfigRepository.setInitialized()
            }
            is WelcomeEvent.RemoveBucket -> {
                mediaRepository.updateBucket(event.bucket.copy(selected = false))
            }
        }
    }
}

sealed interface WelcomeUiState : UiState {
    data object Loading : WelcomeUiState
    data class Success(val initialized: Boolean, val buckets: List<BucketItem> = emptyList()) :
        WelcomeUiState

    data class Error(val error: Throwable) : WelcomeUiState
}

interface WelcomeEvent : UiEvent {
    data class Confirm(
        override val onSuccess: UiEventOnSuccess, override val onFailed: UiEventOnFailed? = null
    ) : WelcomeEvent

    data class RemoveBucket(val bucket: BucketItem) : WelcomeEvent
}