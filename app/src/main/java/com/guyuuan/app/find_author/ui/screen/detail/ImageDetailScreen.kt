@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class
)

package com.guyuuan.app.find_author.ui.screen.detail

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.ui.base.MyApplicationTheme
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Destination<RootGraph>(
    navArgs = ImageDetailScreenNavArgs::class
)
@Composable
fun SharedTransitionScope.ImageDetailScreen(
    navigator: DestinationsNavigator,
    viewModel: ImageDetailViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MyApplicationTheme(darkTheme = true) {
        Scaffold(topBar = { TopBar(modifier = Modifier, navigator = navigator) },
            contentWindowInsets = WindowInsets.Zero,
            bottomBar = {
                BottomBar(onClickShare = {
                    viewModel.dispatch(ImageDetailUiEvent.OnClickShare(context))
                })
            }) { _ ->
            Body(
                uiState = uiState,
                viewModel = viewModel,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    }
}

@Composable
private fun SharedTransitionScope.Body(
    modifier: Modifier = Modifier,
    uiState: ImageDetailUiState,
    viewModel: ImageDetailViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    when (uiState) {
        is ImageDetailUiState.Success -> {
            val images = uiState.images.collectAsLazyPagingItems()
            ImageDetailPager(
                modifier = Modifier.fillMaxSize(),
                images = images,
                initialPage = uiState.navArgs.targetIndex,
                animatedVisibilityScope = animatedVisibilityScope,
            ) { index ->
                if (index < images.itemCount) {
                    images[index]?.let { image ->
                        viewModel.dispatch(
                            ImageDetailUiEvent.OnPageChange(
                                image
                            )
                        )
                    }
                }
            }
        }

        is ImageDetailUiState.Error -> {
            Text(
                uiState.throwable.toString(),
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

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier, onClickShare: () -> Unit
) {
    BottomAppBar(modifier = modifier, containerColor = Color.Transparent) {
        IconButton(onClick = onClickShare) {
            Icon(Icons.Default.Share, contentDescription = null)
        }
    }
}

@Composable
private fun TopBar(modifier: Modifier = Modifier, navigator: DestinationsNavigator) {
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
    animatedVisibilityScope: AnimatedVisibilityScope,
    onPageChanged: (Int) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = initialPage) { images.itemCount }
    LaunchedEffect(pagerState.currentPage, images.itemCount) {
        if (images.itemCount > 0) {
            onPageChanged(pagerState.currentPage)
        }
    }
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

