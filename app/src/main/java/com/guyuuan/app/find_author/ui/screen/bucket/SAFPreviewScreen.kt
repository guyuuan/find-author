package com.guyuuan.app.find_author.ui.screen.bucket

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Destination<RootGraph>(
    navArgs = SAFPreviewArgs::class
)
@Composable
fun SAFPreviewScreen(
    navigator: DestinationsNavigator, viewModel: SAFPreviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStat.collectAsStateWithLifecycle()

    Scaffold {
        Box(
            Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            when (uiState) {
                is SAFPreviewUIState.Loading -> Unit
                is SAFPreviewUIState.Success -> {
                    val images = (uiState as SAFPreviewUIState.Success).images
                    ImageList(Modifier.fillMaxSize(), images)
                }

                is SAFPreviewUIState.Error -> Unit
            }
        }
    }
}

@Composable
private fun ImageList(modifier: Modifier = Modifier, images: List<ImageItem>) {
    LazyVerticalStaggeredGrid(
        StaggeredGridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(images) {
            SubcomposeAsyncImage(
                model = it.uri,
                contentDescription = null,
                error = {e->
                    Text(
                        it.mimeType + "\n"+e.result.throwable.toString(),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
            )

        }
    }
}
