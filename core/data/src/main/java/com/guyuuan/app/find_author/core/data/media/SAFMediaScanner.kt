package com.guyuuan.app.find_author.core.data.media

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
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
 * @createTime: 7/2/24 16:37
 * @description:
 **/

interface SAFMediaScanner {
    fun scanSAFBucketsImages(uri: Uri): Flow<ScanStatus<ImageItem>>
    fun getSAFBucketInfo(uri: Uri): BucketItem
    fun saveSAFPermission(uri: Uri)
    fun removeSAFPermission(uri: Uri)
}

class DefaultSAFMediaScanner @Inject constructor(@ApplicationContext private val context: Context) :
    SAFMediaScanner {
    private val contentResolver: ContentResolver = context.contentResolver
    override fun scanSAFBucketsImages(uri: Uri) = flow<ScanStatus<ImageItem>> {
        val bucketName = uri.path
        val bucketUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            uri, DocumentsContract.getTreeDocumentId(uri)
        )
        contentResolver.query(
            bucketUri, arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED,
            ), null, null, null
        )?.use {
            val displayNameIndex =
                it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            val dateAddedIndex =
                it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
            val idIndex = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
            val mimeTypeIndex =
                it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
            while (it.moveToNext() && currentCoroutineContext().isActive) {
                try {
                    val displayName = it.getString(displayNameIndex)
                    val dateAdded = it.getLong(dateAddedIndex)
                    val id = it.getString(idIndex)
                    val mimeType = it.getString(mimeTypeIndex)
                    if (!mimeType.startsWith("image")) continue
                    val imageUri = DocumentsContract.buildDocumentUriUsingTree(
                        bucketUri, id
                    )
                    emit(
                        ScanStatus.Running(
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
                    )
                } catch (e: Exception) {
                    emit(ScanStatus.Error(e))
                    e.printStackTrace()
                }
            }
            emit(ScanStatus.Done())
        }
    }

    override fun getSAFBucketInfo(uri: Uri): BucketItem {
        val file =
            DocumentFile.fromTreeUri(context, uri) ?: throw Error("Uri[$uri] isn't a tree uri")
//        file
        return BucketItem(
            id = uri.hashCode().toLong(),
            name = file.name ?: "unknown",
            uri = uri.toString(),
            relativePath = file.name ?: "unknown",
            coverUri = null,
            modifiedDate = file.lastModified(),
            type = BucketType.SAF
        )
    }

    override fun saveSAFPermission(uri: Uri) {
        contentResolver.takePersistableUriPermission(
            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }

    override fun removeSAFPermission(uri: Uri) {
        contentResolver.releasePersistableUriPermission(
            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }

}