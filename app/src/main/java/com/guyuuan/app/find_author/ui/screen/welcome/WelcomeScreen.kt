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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.ramcosta.composedestinations.generated.destinations.WelcomeScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

/**
 * @author: Chen
 * @createTime: 6/24/24 11:47
 * @description:
 **/
@Destination<RootGraph>(
    start = true
)
@Composable
fun WelcomeScreen(viewModel: WelcomeViewModel = hiltViewModel(), navigator: DestinationsNavigator) {
    val uiState by viewModel.uiStat.collectAsState()
    val pagerState = rememberPagerState { 3 }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(topBar = {
        TopAppBar(title = { Text("Welcome") })
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            coroutineScope.launch {
                if (pagerState.canScrollForward) {
                    pagerState.animateScrollToPage(pagerState.settledPage + 1)
                } else {
                    navigator.navigate(ChooseBucketsScreenDestination) {
                        popUpTo(WelcomeScreenDestination.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }) {
            AnimatedContent(pagerState.currentPage != 2, label = "fab", transitionSpec = {
                (fadeIn(
                    animationSpec = tween(
                        320,
                    ),
                ) + scaleIn(
                    initialScale = 0.5f, animationSpec = tween(320)
                )).togetherWith(
                    fadeOut(
                        animationSpec = tween(
                            320,
                        )
                    ) + scaleOut(animationSpec = tween(320))
                )
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
    }) {
        HorizontalPager(
            modifier = Modifier.padding(it), state = pagerState
        ) { index ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when (index) {
                    0 -> {
                        Text(
                            "Welcome to Find Author", style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    1 -> {
                        PermissionRequestPage(modifier = Modifier.align(Alignment.Center))
                    }

                    2 -> {
                        Text(
                            "Let's get started", style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

        }
    }
    LaunchedEffect(uiState) {
        val state = uiState
        if ((state is WelcomeUiState.Success) && state.initialized) {
            navigator.navigate(ChooseBucketsScreenDestination)
        }
    }
}

@Composable
fun PermissionRequestPage(modifier: Modifier = Modifier) {
    val mediaPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(android.Manifest.permission.READ_MEDIA_IMAGES)
    } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    } else {
        return
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