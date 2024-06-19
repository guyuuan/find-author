/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guyuuan.app.find_author.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.guyuuan.app.find_author.core.data.ImageRepository
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.data.media.MediaStoreScanner
import com.guyuuan.app.find_author.feature.home.ui.HomeUiState.Error
import com.guyuuan.app.find_author.feature.home.ui.HomeUiState.Loading
import com.guyuuan.app.find_author.feature.home.ui.HomeUiState.Success
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    private val scanner: MediaStoreScanner
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = imageRepository
        .getAllImages().map<List<ImageItem>, HomeUiState> { Success(data = it) }
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

   init {
       viewModelScope.launch (Dispatchers.IO){
           scanner.scanBucketImages(1).collect{
               imageRepository.addImage(it)
           }
       }
   }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Error(val throwable: Throwable) : HomeUiState
    data class Success(val data: List<ImageItem>) : HomeUiState
}
