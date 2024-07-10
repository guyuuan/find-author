package com.guyuuan.app.find_author.ui.screen.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.SubcomposeAsyncImage
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.ui.compoments.Transform
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ChooseBucketsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

/**
 * @author: Chen
 * @createTime: 6/26/24 15:52
 * @description:
 **/
@Destination<RootGraph>
@Composable
fun HomeScreen(navigator: DestinationsNavigator, viewModel: HomeViewModel = hiltViewModel()) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigator.navigate(ChooseBucketsScreenDestination)
            }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) {
        HomeScreen(Modifier.padding(it), viewModel)
    }
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier, viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Crossfade(uiState, modifier = modifier, label = "HomeScreen") { state ->
        when (state) {
            is HomeUiState.Loading -> {
            }

            is HomeUiState.Success -> {
                ImageGrid(modifier = Modifier, images = state.images)
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
}

@Composable
private fun ImageGrid(modifier: Modifier = Modifier, images: Flow<PagingData<ImageItem>>) {
    val pagingData = images.collectAsLazyPagingItems()
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(count = pagingData.itemCount) { index ->
            val image = pagingData[index] ?: return@items
            Transform(
                modifier = Modifier.fillMaxSize(),
                key = image.id,
                delay = 300
            ) {
                ImageGridItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.small),
                    image = image
                )
            }
        }
    }
}

@Composable
fun ImageGridItem(modifier: Modifier = Modifier, image: ImageItem) {
    SubcomposeAsyncImage(
        model = image, contentDescription = null,
        onError = {
            Timber.tag("ImageGridItem").e(it.result.throwable)
        },
        error = { e ->
            Text(
                image.mimeType + "\n" + e.result.throwable.toString(),
                color = MaterialTheme.colorScheme.error
            )
        }, contentScale = ContentScale.Crop, modifier = modifier
    )
}