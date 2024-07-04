@file:OptIn(ExperimentalMaterial3Api::class)

package com.guyuuan.app.find_author.ui.screen.bucket

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.SubcomposeAsyncImage
import com.guyuuan.app.find_author.core.data.model.BucketItem
import com.guyuuan.app.find_author.core.ui.compoments.Transform
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

/**
 * @author: Chen
 * @createTime: 6/11/24 10:30
 * @description:
 **/

@Destination<RootGraph>(
//    start = true
)
@Composable
fun ChooseBucketsScreen(
    viewModel: ChooseBucketsViewModel = hiltViewModel(), navigator: DestinationsNavigator
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Choose buckets") }, navigationIcon = {
            IconButton(onClick = {
                navigator.navigateUp()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        })
    }) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            when (val state = uiState) {
                is ChooseBucketsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = state.error.toString(),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                is ChooseBucketsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LinearProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }

                is ChooseBucketsUiState.Success -> {
                    val buckets = state.buckets.collectAsLazyPagingItems()
                    SuccessScreen(buckets = buckets) { bucket ->
                        viewModel.dispatch(ChooseBucketsEvent.SelectBucket(bucket.copy(selected = !bucket.selected)))
                    }
                }
            }
        }
    }
}


@Composable
private fun SuccessScreen(
    modifier: Modifier = Modifier,
    buckets: LazyPagingItems<BucketItem>,
    onItemClick: (BucketItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 8.dp)
    ) {
        if (buckets.loadState.refresh is LoadState.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        items(key = buckets.itemKey { it.id }, count = buckets.itemCount) {
            val item = buckets[it] ?: return@items
            Transform(
                modifier = Modifier.fillMaxSize(),
                key = item.id,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                BucketListItem(modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onItemClick(item) }
                    .padding(8.dp), item = item)
            }
        }
    }
}

@Composable
private fun BucketListItem(modifier: Modifier = Modifier, item: BucketItem) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box {
            SubcomposeAsyncImage(model = item.coverUri,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
                loading = {
                    CircularProgressIndicator()
                },
                onError = { e ->
                    Timber.tag("Coil").e(e.result.throwable, "Failed to load image: ")
                })
            this@Column.AnimatedVisibility(
                item.selected,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(8.dp, (-8).dp),
            ) {
                Icon(
                    Icons.Default.CheckCircle, contentDescription = "Selected",

                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Text(text = item.relativePath, modifier = Modifier.padding(top = 8.dp))
    }
}