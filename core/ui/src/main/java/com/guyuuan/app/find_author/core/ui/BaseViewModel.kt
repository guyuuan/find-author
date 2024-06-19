package com.guyuuan.app.find_author.core.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

/**
 * @author: Chen
 * @createTime: 6/11/24 17:08
 * @description:
 **/
 abstract  class BaseViewModel<State,Event>:ViewModel() {
     abstract val uiStat:StateFlow<State>

     protected abstract fun onEvent(event: Event)

     fun dispatch(event: Event){
         onEvent(event)
     }
}