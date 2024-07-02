@file:OptIn(ExperimentalFoundationApi::class)

package com.guyuuan.app.find_author.ui.screen.bucket

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

/**
 * @author: Chen
 * @createTime: 7/1/24 15:52
 * @description:
 **/
data class SAFPreviewArgs(val uri: String)

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(
    navArgs = SAFPreviewArgs::class
)
@Composable
fun SAFPreviewScreen(
    navigator: DestinationsNavigator, viewModel: SAFPreviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val title = remember(uiState) {
        when (val state = uiState) {
            is SAFPreviewUIState.Success -> state.bucket.name
            else -> "Loading..."
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                }, actions = {
                    IconButton(onClick = {
                        viewModel.dispatch(SAFPreviewUIEvent.Confirm(onSuccess = {
                            navigator.navigateUp()
                        }))
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Confirm"
                        )
                    }
                }
            )
        }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            when (uiState) {
                is SAFPreviewUIState.Loading -> Unit
                is SAFPreviewUIState.Success -> {
                    val state = (uiState as SAFPreviewUIState.Success)
                    PreviewScreen(Modifier.fillMaxSize(), state.running, state.images)
                }

                is SAFPreviewUIState.Error -> Unit
            }
        }
    }
}

@Composable
private fun PreviewScreen(
    modifier: Modifier = Modifier, isRunning: Boolean, images: List<ImageItem>
) {
    Column(modifier) {
        AnimatedVisibility(
            isRunning,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .padding(
                        horizontal = 24.dp,
                        vertical = 8.dp
                    )
                    .fillMaxWidth()
            )
        }
        AnimatedVisibility(images.isNotEmpty(), modifier = Modifier.weight(1f)) {
            ImageList(modifier = Modifier.fillMaxSize(), images = images)
        }
    }
}

@Composable
private fun ImageList(modifier: Modifier = Modifier, images: List<ImageItem>) {
    LazyVerticalStaggeredGrid(
        StaggeredGridCells.Fixed(4),
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(images) {
            SubcomposeAsyncImage(
                model = it.uri,
                contentDescription = null,
                error = { e ->
                    Text(
                        it.mimeType + "\n" + e.result.throwable.toString(),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .animateItemPlacement()
            )

        }
    }
}
