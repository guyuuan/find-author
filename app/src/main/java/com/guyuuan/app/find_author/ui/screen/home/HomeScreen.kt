@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.guyuuan.app.find_author.ui.screen.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.data.model.PagingUiMode
import com.guyuuan.app.find_author.core.ui.compoments.CoilImage
import com.guyuuan.app.find_author.core.ui.compoments.TransformBox
import com.guyuuan.app.find_author.core.ui.util.plus
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ChooseBucketsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ImageDetailScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

/**
 * @author: guyuuan
 * @createTime: 6/26/24 15:52
 * @description:
 **/
@Destination<RootGraph>
@Composable
fun SharedTransitionScope.HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigator.navigate(ChooseBucketsScreenDestination)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }, contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        HomeScreen(
            Modifier.padding(top = it.calculateTopPadding()),
            viewModel,
            navigator,
            animatedVisibilityScope
        )
    }
}

@Composable
private fun SharedTransitionScope.HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    navigator: DestinationsNavigator,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val safeContentPadding = WindowInsets.safeContent.asPaddingValues()
    when (val state = uiState) {
        is HomeUiState.Loading -> {
        }

        is HomeUiState.Success -> {
            val images = state.images.collectAsLazyPagingItems()
            ImageGrid(
                modifier = modifier then Modifier.consumeWindowInsets(WindowInsets.safeContent),
                safeContentPadding = safeContentPadding,
                images = images,
                onImageClick = {
                    navigator.navigate(ImageDetailScreenDestination.invoke(targetIndex = it))
                },
                animatedVisibilityScope = animatedVisibilityScope
            )
        }

        is HomeUiState.Error -> {
            Text(
                state.error.toString(),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxSize(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SharedTransitionScope.ImageGrid(
    modifier: Modifier = Modifier,
    images: LazyPagingItems<PagingUiMode<ImageItem>>,
    safeContentPadding: PaddingValues,
    onImageClick: (index: Int) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp) + safeContentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(count = images.itemCount, span = {
            val item = images[it]
            if (item is PagingUiMode.Item) GridItemSpan(1) else GridItemSpan(maxLineSpan)
        }) { index ->
            val image = images[index] ?: return@items
            when (image) {
                is PagingUiMode.Item -> {
                    TransformBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .sharedElement(
                                rememberSharedContentState(image.realIndex),
                                animatedVisibilityScope
                            ),
                        enter = scaleIn(),
                        exit = scaleOut(),
                        key = image.data.id,
                    ) {
                        ImageGridItem(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(MaterialTheme.shapes.small)
                                .clickable {
                                    onImageClick(image.realIndex)
                                }
                                .sharedElement(
                                    rememberSharedContentState(key = "image/${image.realIndex}"),
                                    animatedVisibilityScope,
                                    boundsTransform = { _, _ ->
                                        tween(durationMillis = 1000)
                                    }
                                ),
                            image = image.data,
                        )
                    }
                }

                is PagingUiMode.Header -> TransformBox(
                    key = image.timeString,
                    modifier = Modifier.fillMaxWidth(),
                    delay = 100,
                ) {
                    Text(
                        image.timeString,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }

        }
    }
}

@Composable
fun ImageGridItem(modifier: Modifier = Modifier, image: ImageItem) {
    CoilImage(
        model = (image.thumbnailUri ?: image.uri).toUri(),
        useThumbnails = true,
        contentDescription = null,
        onError = {
            Timber.tag("ImageGridItem").e(it.result.throwable)
        },
        error = { e ->
            Text(
                image.mimeType + "\n" + e.result.throwable.toString(),
                color = MaterialTheme.colorScheme.error
            )
        },
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}