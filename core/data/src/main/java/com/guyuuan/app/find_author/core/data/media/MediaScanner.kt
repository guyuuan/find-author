package com.guyuuan.app.find_author.core.data.media

import android.content.ContentResolver
import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.guyuuan.app.find_author.core.data.model.BucketItem
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.database.dao.ImageDao
import com.guyuuan.app.find_author.core.database.model.BucketType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

/**
 * @author: guyuuan
 * @createTime: 6/6/24 17:50
 * @description:
 **/
private const val TAG = "MediaScanner"

sealed class ScanStatus<T> {
    data class Done<T>(val throwable: Throwable? = null) : ScanStatus<T>()
    class Idle<T> : ScanStatus<T>()
    data class Running<T>(val data: T) : ScanStatus<T>()
    data class Error<T>(val throwable: Throwable) : ScanStatus<T>()
}

interface MediaScanner : MediaStoreScanner, SAFMediaScanner, ShizukuMediaScanner {
    fun Flow<List<BucketItem>>.scamImages(
        update: suspend (image: ImageItem) -> Unit
    ): Flow<ScanStatus<BucketItem>>

    fun Flow<List<BucketItem>>.checkImagesExist(): Flow<Unit>

}

class DefaultMediaScanner @Inject constructor(
    mediaStore: MediaStoreScanner,
    saf: SAFMediaScanner,
    shizuku: ShizukuMediaScanner,
    private val imageDao: ImageDao,
    @ApplicationContext context: Context
) : MediaScanner, MediaStoreScanner by mediaStore, SAFMediaScanner by saf,
    ShizukuMediaScanner by shizuku {
    private val contentResolver: ContentResolver = context.contentResolver

    override fun Flow<List<BucketItem>>.scamImages(
        update: suspend (image: ImageItem) -> Unit
    ): Flow<ScanStatus<BucketItem>> = transform { buckets ->

        for (bucket in buckets) {
            emit(ScanStatus.Running(bucket))
            try {
                when (bucket.type) {
                    BucketType.MediaStore -> {
                        scanMediaStoreBucketImages(bucket.id).collect {
                            if (it is ScanStatus.Running) {
                                update(it.data)
                            }
                        }
                    }

                    BucketType.SAF -> {
                        check(bucket.uri != null) {
                            "bucket uri is null"
                        }
                        scanSAFBucketsImages(bucket.uri!!.toUri()).collect {
                            if (it is ScanStatus.Running) {
                                update(it.data.copy(bucketId = bucket.id))
                            }
                        }
                    }

                    BucketType.Shizuku -> {

                    }

                    else -> {}
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ScanStatus.Error(e))
            }
        }
    }.onCompletion {
        emit(ScanStatus.Done())
    }.flowOn(Dispatchers.IO)

    override  fun Flow<List<BucketItem>>.checkImagesExist() = transform { buckets ->
        for (bucket in buckets) {
            val count = imageDao.getBucketImageCount(bucket.id)
            repeat(count) { index ->
                try {
                    val image = imageDao.getBucketImage(bucket.id, index)
                    val exist = checkImageExist(image)
                    if (!exist) {
                        imageDao.deleteImage(image)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "checkImageExist: ", e)
                }

            }
        }
        emit(Unit)
    }.flowOn(Dispatchers.IO)

    private fun checkImageExist(image: ImageItem): Boolean {
        try {
            contentResolver.openInputStream(image.uri.toUri()).use { }
            return true
        } catch (e: Exception) {
            return false
        }
    }
}

