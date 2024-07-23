package com.guyuuan.app.find_author.feature.home.ui.screen

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.guyuuan.app.find_author.core.data.MediaRepository
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * @author: guyuuan
 * @createTime: 6/12/24 14:47
 * @description:
 **/
@HiltViewModel
class BucketViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val savedStateHandle: SavedStateHandle
) :BaseViewModel<BucketUiState, BucketEvent>() {
    override val uiStat: StateFlow<BucketUiState>
        get() = TODO()

    override fun onEvent(event: BucketEvent) {
        TODO("Not yet implemented")
    }
}

interface BucketUiState{
    data object Loading:BucketUiState
    data class Success(val data:PagingData<ImageItem>):BucketUiState
    data class Error(val throws: Throws):BucketUiState
}

interface BucketEvent {

}