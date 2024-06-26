package com.guyuuan.app.find_author.core.data.media

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.guyuuan.app.find_author.core.data.model.BucketItem
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.database.model.BucketType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 6/6/24 17:50
 * @description:
 **/
interface MediaScanner : MediaStoreScanner, SAFMediaScanner, ShizukuMediaScanner

class DefaultMediaScanner @Inject constructor(
    mediaStore: MediaStoreScanner, saf: SAFMediaScanner, shizuku: ShizukuMediaScanner
) : MediaScanner, MediaStoreScanner by mediaStore, SAFMediaScanner by saf,
    ShizukuMediaScanner by shizuku


interface MediaStoreScanner {


    fun scanMediaStoreBucketImages(bucketId: Long): Flow<ImageItem>

    fun scanMediaStoreBuckets(): Flow<BucketItem>

}

interface SAFMediaScanner {
    fun scanSAFBucketsImages(uri: Uri): Flow<ImageItem>
}

interface ShizukuMediaScanner {
    fun scanShizukuBucketsImages(uri: Uri): Flow<ImageItem>
}

class AndroidMediaStoreScanner @Inject constructor(@ApplicationContext context: Context) :
    MediaStoreScanner {
    private val contentResolver: ContentResolver = context.contentResolver

    override fun scanMediaStoreBuckets() = flow<BucketItem> {
        val buckets = mutableSetOf<Long>()
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_TAKEN
            ),
            "${MediaStore.Images.Media.MIME_TYPE} = ? or ${MediaStore.Images.Media.MIME_TYPE} = ? or ${MediaStore.Images.Media.MIME_TYPE} = ?",
            arrayOf("image/jpeg", "image/png", "image/gif"),
            "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        )?.use {
            val bucketIdIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameIndex =
                it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val relativePathIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
            val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            while (it.moveToNext() && currentCoroutineContext().isActive) {
                try {
                    val bucketId = it.getLong(bucketIdIndex)
                    val bucketName = it.getString(bucketNameIndex)
                    val relativePath = it.getString(relativePathIndex)
                    val coverId = it.getLong(idIndex)
                    val date = it.getLong(dateIndex)
                    if (!buckets.contains(bucketId)) {
                        buckets.add(bucketId)
                        emit(
                            BucketItem(
                                id = bucketId,
                                name = bucketName,
                                relativePath = relativePath,
                                modifiedDate = date,
                                coverUri = ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, coverId
                                ).toString(),
                                type = BucketType.MediaStore
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    override fun scanMediaStoreBucketImages(bucketId: Long): Flow<ImageItem> = flow {
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media.MIME_TYPE
            ),
            "${MediaStore.Images.Media.BUCKET_ID} = ? and (${MediaStore.Images.Media.MIME_TYPE} = ? or ${MediaStore.Images.Media.MIME_TYPE} = ? or ${MediaStore.Images.Media.MIME_TYPE} = ?)",
            arrayOf(bucketId.toString(), "image/jpeg", "image/png", "image/gif"),
            "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        )?.use {
            val bucketIdIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameIndex =
                it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val displayNameIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateAddedIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val pathIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val relativePathIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
            val mimeTypeIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            while (it.moveToNext()) {
                try {
                    val bucketID = it.getLong(bucketIdIndex)
                    val bucketName = it.getString(bucketNameIndex)
                    val displayName = it.getString(displayNameIndex)
                    val dateAdded = it.getLong(dateAddedIndex)
                    val id = it.getLong(idIndex)
                    val path = it.getString(pathIndex)
                    val relativePath = it.getString(relativePathIndex)
                    val mimeType = it.getString(mimeTypeIndex)
                    val uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                    )
                    emit(
                        ImageItem(
                            id = id.toString(),
                            name = displayName,
                            uri = uri.toString(),
                            path = path,
                            mimeType = mimeType,
                            dateAdded = dateAdded,
                            bucketId = bucketID,
                            bucketName = bucketName,
                            relativePath = relativePath,
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}

class DefaultSAFMediaScanner @Inject constructor(@ApplicationContext context: Context) :
    SAFMediaScanner {
    private val contentResolver: ContentResolver = context.contentResolver
    override fun scanSAFBucketsImages(uri: Uri) = flow<ImageItem> {
        val bucketName = uri.path
        val bucketUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            uri, DocumentsContract.getTreeDocumentId(uri)
        )
        contentResolver.query(
            bucketUri,
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
            ),
            null,null,
            "${DocumentsContract.Document.COLUMN_LAST_MODIFIED} DESC"
        )?.use {
            val displayNameIndex = it.getColumnIndexOrThrow(                DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            val dateAddedIndex = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
            val idIndex = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
            val mimeTypeIndex = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
            while (it.moveToNext()) {
                try {
                    val displayName = it.getString(displayNameIndex)
                    val dateAdded = it.getLong(dateAddedIndex)
                    val id = it.getString(idIndex)
                    val mimeType = it.getString(mimeTypeIndex)
                    if(!mimeType.startsWith("image")) continue
                    val imageUri = DocumentsContract.buildDocumentUriUsingTree(
                        bucketUri, id
                    )
                    emit(
                        ImageItem(
                            id = id,
                            name = displayName,
                            uri = imageUri.toString(),
                            mimeType = mimeType,
                            dateAdded = dateAdded,
                            bucketId = 0L,
                            bucketName = bucketName,
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    }
}

class DefaultShizukuMediaScanner @Inject constructor() : ShizukuMediaScanner {
    override fun scanShizukuBucketsImages(uri: Uri) = flow<ImageItem> { }
}
