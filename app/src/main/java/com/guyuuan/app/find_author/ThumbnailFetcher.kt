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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val bitmap = contentResolver.loadThumbnail(
                uri,
                Size(options.size.width.pxOrElse { 400 }, options.size.height.pxOrElse { 400 }),
                null
            )
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//            val buffer = ByteBuffer.allocateDirect(bitmap.byteCount)
//            bitmap.copyPixelsToBuffer(buffer)
//            check(buffer.hasArray()  ){
//                "Buffer has no array."
//            }
            return SourceResult(
                source = ImageSource(
                    source = ByteArrayInputStream(baos.toByteArray()).source().buffer(),
//                    source = ByteArrayInputStream(buffer.array())
//                        .source().buffer(),
                    context = options.context,
                    metadata = ContentMetadata(uri)
                ),
                mimeType = contentResolver.getType(uri),
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