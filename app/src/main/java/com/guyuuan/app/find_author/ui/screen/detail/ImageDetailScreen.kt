@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class
)

package com.guyuuan.app.find_author.ui.screen.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.ui.compoments.CoilImage
import com.guyuuan.app.find_author.core.ui.util.Zero
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

/**
 * @author: guyuuan
 * @createTime: 7/23/24 09:42
 * @description:
 **/

data class ImageDetailScreenNavArgs(val targetIndex: Int = 0)

@Destination<RootGraph>(
    navArgs = ImageDetailScreenNavArgs::class
)
@Composable
fun SharedTransitionScope.ImageDetailScreen(
    navigator: DestinationsNavigator, viewModel: ImageDetailViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = { TopBar(modifier = Modifier, navigator = navigator) },
        contentWindowInsets = WindowInsets.Zero
    ) {
        when (val state = uiState) {
            is ImageDetailUiState.Success -> {
                val images = state.images.collectAsLazyPagingItems()
                ImageDetailPager(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = it.calculateTopPadding()),
                    images = images,
                    initialPage = state.navArgs.targetIndex,
                    animatedVisibilityScope
                )
            }

            is ImageDetailUiState.Error -> {
                Text(
                    state.throwable.toString(),
                    modifier = Modifier,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LinearProgressIndicator()
                }
            }

        }
    }
}


@Composable
fun TopBar(modifier: Modifier = Modifier, navigator: DestinationsNavigator) {
    TopAppBar(modifier = modifier,
        title = { Text(text = "") },
        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Transparent),
        navigationIcon = {
            IconButton(onClick = {
                navigator.navigateUp()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        })
}

@Composable
private fun SharedTransitionScope.ImageDetailPager(
    modifier: Modifier = Modifier,
    images: LazyPagingItems<ImageItem>,
    initialPage: Int,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val pagerState = rememberPagerState(initialPage = initialPage) { images.itemCount }
    HorizontalPager(pagerState, modifier = modifier) { index ->
        images[index]?.let { image ->
            CoilImage(
                model = image.uri.toUri(),
                useThumbnails = false,
                modifier = Modifier
                    .fillMaxSize()
                    .sharedElement(
                        rememberSharedContentState(key = "image/${index}"),
                        animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = 1000)
                        },
                    ),
                contentScale = ContentScale.FillWidth,
                contentDescription = null
            )
        }
    }
}

