package com.guyuuan.app.find_author.ui.screen.welcome

import androidx.lifecycle.viewModelScope
import com.guyuuan.app.find_author.core.data.AppConfigRepository
import com.guyuuan.app.find_author.core.ui.BaseViewModel
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
 * @createTime: 6/24/24 14:20
 * @description:
 **/
@HiltViewModel
class WelcomeViewModel @Inject constructor(private val appConfigRepository: AppConfigRepository) :
    BaseViewModel<WelcomeUiState, WelcomeEvent>() {
    override val uiStat: StateFlow<WelcomeUiState> = appConfigRepository.getInitializedFlow()
        .map<Boolean, WelcomeUiState> { WelcomeUiState.Success(it) }.catch {
            emit(WelcomeUiState.Error(it))
        }.stateIn(viewModelScope, SharingStarted.Eagerly, WelcomeUiState.Loading)

    override fun onEvent(event: WelcomeEvent) {
        when (event) {
            WelcomeEvent.Confirm -> {
                viewModelScope.launch(Dispatchers.IO) {
                    appConfigRepository.setInitialized()
                }
            }
        }
    }
}

sealed interface WelcomeUiState {
    data object Loading : WelcomeUiState
    data class Success(val initialized: Boolean) : WelcomeUiState
    data class Error(val error: Throwable) : WelcomeUiState
}

interface WelcomeEvent {
    data object Confirm : WelcomeEvent
}