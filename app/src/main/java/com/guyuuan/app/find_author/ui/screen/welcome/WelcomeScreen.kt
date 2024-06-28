@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalPermissionsApi::class
)

package com.guyuuan.app.find_author.ui.screen.welcome

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ChooseBucketsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.WelcomeScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * @author: Chen
 * @createTime: 6/24/24 11:47
 * @description:
 **/

private val pageList =
    listOf(WelcomeStep.First, WelcomeStep.RequestPermission, WelcomeStep.ChooseBuckets)

@Destination<RootGraph>(
    start = true
)
@Composable
fun WelcomeScreen(viewModel: WelcomeViewModel = hiltViewModel(), navigator: DestinationsNavigator) {
    val uiState by viewModel.uiStat.collectAsState()
    val pagerState = rememberPagerState { pageList.size }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(topBar = {
        TopAppBar(title = { Text("Welcome") })
    }, floatingActionButton = {
        FloatActionButton(pagerState, coroutineScope, viewModel, navigator)
    }) {
        with(remember { WelcomeScreenScopeImpl(coroutineScope, navigator) }) {
            HorizontalPager(
                modifier = Modifier.padding(it), state = pagerState, userScrollEnabled = false
            ) { index ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    pageList[index].Content(modifier = Modifier)
                }

            }
        }
    }
    LaunchedEffect(uiState) {
        val state = uiState
        if ((state is WelcomeUiState.Success) && state.initialized) {
            navigator.navigate(HomeScreenDestination) {
                popUpTo(WelcomeScreenDestination) {
                    inclusive = true
                }
            }
        }
    }
}

@Composable
fun FloatActionButton(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    viewModel: WelcomeViewModel,
    navigator: DestinationsNavigator
) {
    val currentPage = remember(pagerState.currentPage) { pageList[pagerState.currentPage] }

    FloatingActionButton(onClick = {
        coroutineScope.launch {
            if (currentPage.onClickNext()) {
                if (pagerState.canScrollForward) {
                    pagerState.animateScrollToPage(pagerState.settledPage + 1)
                } else {
                    viewModel.dispatch(WelcomeEvent.Confirm(success = {
                        navigator.navigate(HomeScreenDestination) {
                            popUpTo(WelcomeScreenDestination.route) {
                                inclusive = true
                            }
                        }
                    }))
                }
            }
        }
    }) {
        AnimatedContent(pagerState.currentPage != 2, label = "fab", transitionSpec = {
            fadeIn(
                animationSpec = tween(
                    320,
                ),
            ) + scaleIn(
                initialScale = 0.5f, animationSpec = tween(320)
            ) togetherWith fadeOut(
                animationSpec = tween(
                    320,
                )
            ) + scaleOut(animationSpec = tween(320))

        }) {
            Icon(
                if (it) {
                    Icons.AutoMirrored.Filled.ArrowForward
                } else {
                    Icons.Default.Done
                }, null
            )
        }
    }
}

private interface WelcomeScreenScope {
    val coroutineScope: CoroutineScope
    val navigator: DestinationsNavigator
}

private class WelcomeScreenScopeImpl(
    override val coroutineScope: CoroutineScope, override val navigator: DestinationsNavigator
) : WelcomeScreenScope {}

private abstract class WelcomeStep {

    abstract fun onClickNext(): Boolean

    context(WelcomeScreenScope)
    @Composable
    abstract fun Content(modifier: Modifier)

    object First : WelcomeStep() {
        override fun onClickNext() = true

        context(WelcomeScreenScope)
        @Composable
        override fun Content(modifier: Modifier) {
            Text("Welcome to Find Author", modifier = modifier)
        }

    }

    object RequestPermission : WelcomeStep() {
        //
        private var permissionState: Boolean = false
        override fun onClickNext(): Boolean {
            Timber.d("permissionState = $permissionState")
            return permissionState
        }

        context(WelcomeScreenScope)
        @Composable
        override fun Content(modifier: Modifier) {
            val mediaPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                rememberPermissionState(android.Manifest.permission.READ_MEDIA_IMAGES) {
                    permissionState = it
                }
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE) {
                    permissionState = it
                }
            } else {
                return
            }

            LaunchedEffect(mediaPermission) {
                permissionState = mediaPermission.status.isGranted
            }

            ElevatedButton(modifier = modifier, onClick = {
                if (!mediaPermission.status.isGranted) {
                    mediaPermission.launchPermissionRequest()
                }
            }) {
                Icon(
                    if (mediaPermission.status.isGranted) {
                        Icons.Default.Done
                    } else {
                        Icons.Default.Close
                    }, null
                )
                Text(
                    text = if (mediaPermission.status.isGranted) {
                        "Granted"
                    } else {
                        "Request Permission"
                    }
                )
            }
        }
    }

    object ChooseBuckets : WelcomeStep() {
        override fun onClickNext() = true

        context(WelcomeScreenScope)
        @Composable
        override fun Content(modifier: Modifier) {
            TextButton(modifier = modifier, onClick = {
                navigator.navigate(ChooseBucketsScreenDestination)
            }) {
                Text(
                    "Let's get started choose your buckets",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}