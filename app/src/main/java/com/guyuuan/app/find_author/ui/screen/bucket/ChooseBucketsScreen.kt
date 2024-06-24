package com.guyuuan.app.find_author.ui.screen.bucket

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
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
    viewModel: ChooseBucketsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val uiState by viewModel.uiStat.collectAsStateWithLifecycle()
    AnimatedContent(targetState = uiState, label = "chooseBuckets") { state ->

        when (state) {
            is ChooseBucketsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = state.error.toString(), modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            is ChooseBucketsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    LinearProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

            is ChooseBucketsUiState.Success -> {
                SuccessScreen(uiState = state)
            }
        }
    }
}


@Composable
private fun SuccessScreen(modifier: Modifier = Modifier, uiState: ChooseBucketsUiState.Success) {
    if (uiState.buckets.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "No buckets found", modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 8.dp)
    ) {
        items(uiState.buckets) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SubcomposeAsyncImage(
                    model = it.coverUri?.toString(),
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop,
                    loading = {
                        CircularProgressIndicator()
                    },
                    onError = {
                        Timber.tag("Coil").e(it.result.throwable, "Failed to load image: ")
                    }
                )
                Text(text = it.relativePath, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}