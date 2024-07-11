package com.guyuuan.app.find_author

import android.graphics.BitmapFactory
import android.os.Build
import android.util.Size
import androidx.core.graphics.drawable.toDrawable
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.decode.ContentMetadata
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import coil.size.pxOrElse
import com.guyuuan.app.find_author.core.ui.compoments.ThumbnailUri
import okio.buffer
import okio.source

/**
 * @author: Chen
 * @createTime: 7/9/24 16:23
 * @description:
 **/

class ThumbnailFetcher(
    private val data: ThumbnailUri,
    private val options: Options,
) : Fetcher {
    @OptIn(ExperimentalCoilApi::class)
    override suspend fun fetch(): FetchResult {
        val contentResolver = options.context.contentResolver
        val uri = data.uri
        val option = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, option)
        val width = option.outWidth
        val height = option.outHeight
        val displayWidth = options.size.width.pxOrElse {
            throw IllegalArgumentException("No size specified")
        }
        val displayHeight = height * displayWidth / width
        val size = Size(displayWidth, displayHeight)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val bitmap = contentResolver.loadThumbnail(
                uri,
                size,
                null
            )
            return DrawableResult(
                drawable = bitmap.toDrawable(options.context.resources),
                isSampled = false,
                dataSource = DataSource.DISK
            )
        } else {
            val input = contentResolver.openInputStream(uri)
            return SourceResult(
                source = ImageSource(
                    source = input!!.source().buffer(),
                    context = options.context,
                    metadata = ContentMetadata(uri)
                ),
                mimeType = contentResolver.getType(uri),
                dataSource = DataSource.DISK
            )
        }
    }

    class Factory : Fetcher.Factory<ThumbnailUri> {
        override fun create(
            data: ThumbnailUri,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher {
            return ThumbnailFetcher(data, options)
        }

    }
}