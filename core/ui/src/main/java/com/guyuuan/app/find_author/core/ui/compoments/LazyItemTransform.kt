package com.guyuuan.app.find_author.core.ui.compoments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * @author: guyuuan
 * @createTime: 7/3/24 11:00
 * @description:
 **/

@Composable
fun LazyGridItemScope.TransformBox(
    modifier: Modifier = Modifier,
    key: Any? = null,
    delay: Long = 0L,
    enter: EnterTransition = fadeIn() + expandIn(),
    exit: ExitTransition = fadeOut() + shrinkOut(),
    content: @Composable context(AnimatedVisibilityScope) LazyGridItemScope.() -> Unit
) = Transform(
    modifier,
    key = key,
    delay = delay,
    enter = enter,
    exit = exit,
    content = content,
)


@Composable
fun LazyStaggeredGridItemScope.TransformBox(
    modifier: Modifier = Modifier,
    key: Any? = null,
    delay: Long = 0L,
    enter: EnterTransition = fadeIn() + expandIn(),
    exit: ExitTransition = fadeOut() + shrinkOut(),
    content: @Composable context(AnimatedVisibilityScope) LazyStaggeredGridItemScope.() -> Unit
) = Transform(
    modifier,
    key = key,
    delay = delay,
    enter = enter,
    exit = exit,
    content = content,
)


@Composable
fun LazyItemScope.TransformBox(
    modifier: Modifier = Modifier,
    key: Any? = null,
    delay: Long = 0L,
    enter: EnterTransition = fadeIn() + expandIn(),
    exit: ExitTransition = fadeOut() + shrinkOut(),
    content: @Composable context(AnimatedVisibilityScope) LazyItemScope.() -> Unit
) = Transform(
    modifier,
    key = key,
    delay = delay,
    enter = enter,
    exit = exit,
    content = content,
)


@Composable
private fun <T> T.Transform(
    modifier: Modifier = Modifier,
    key: Any?,
    delay: Long,
    enter: EnterTransition,
    exit: ExitTransition,
    content: @Composable context(AnimatedVisibilityScope) T.() -> Unit
) {
    val state = rememberSaveable(key, saver = TransitionSaver) {
        MutableTransitionState(false)
    }
    AnimatedVisibility(state, modifier = modifier, enter = enter, exit = exit) {
        this.content(this@Transform)
    }
    LaunchedEffect(key) {
        withContext(Dispatchers.Default) {
            delay(delay)
            state.targetState = true
        }
    }
}

private val TransitionSaver =
    Saver<MutableTransitionState<Boolean>, Boolean>(save = { it.currentState },
        restore = { MutableTransitionState(it) })