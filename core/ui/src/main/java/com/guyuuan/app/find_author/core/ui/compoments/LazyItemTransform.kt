package com.guyuuan.app.find_author.core.ui.compoments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

/**
 * @author: Chen
 * @createTime: 7/3/24 11:00
 * @description:
 **/
@Composable
fun <T> T.Transform(
    modifier: Modifier = Modifier,
    key: Any? = null,
    enter: EnterTransition = fadeIn() + expandIn(),
    exit: ExitTransition = fadeOut() + shrinkOut(),
    content: @Composable T.() -> Unit
) {
    val state = rememberSaveable(key, saver = TransitionSaver) {
        MutableTransitionState(false)
    }
    AnimatedVisibility(state, modifier = modifier, enter = enter, exit = exit) {
        content()
    }
    LaunchedEffect(key) {
        state.targetState = true
    }
}

private val TransitionSaver = Saver<MutableTransitionState<Boolean>, Boolean>(
    save = { it.currentState },
    restore = { MutableTransitionState(it) }
)