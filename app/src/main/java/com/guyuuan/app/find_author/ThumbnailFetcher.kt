package com.guyuuan.app.find_author

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.Size
import android.view.PixelCopy
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
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
import com.guyuuan.app.find_author.core.data.model.ImageItem
import okio.buffer
import okio.sink
import okio.source
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * @author: Chen
 * @createTime: 7/9/24 16:23
 * @description:
 **/

class ThumbnailFetcher(
    private val data: ImageItem,
    private val options: Options,
) : Fetcher {
    @OptIn(ExperimentalCoilApi::class)
    override suspend fun fetch(): FetchResult? {
        val contentResolver = options.context.contentResolver
        val uri = data.uri.toUri()
        val option = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(contentResolver.openInputStream(uri),null,option)
        val width = option.outWidth
        val height = option.outHeight
        val displayWidth = options.size.width.pxOrElse {
            throw IllegalArgumentException("No size specified")
        }
        val displayHeight = height*displayWidth/width
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
        return null
    }

    class Factory : Fetcher.Factory<ImageItem> {
        override fun create(data: ImageItem, options: Options, imageLoader: ImageLoader): Fetcher {
            return ThumbnailFetcher(data, options)
        }

    }
}