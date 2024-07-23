package com.guyuuan.app.find_author.core.data.media

import androidx.core.net.toUri
import com.guyuuan.app.find_author.core.data.model.BucketItem
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.database.model.Bucket
import com.guyuuan.app.find_author.core.database.model.BucketType
import com.guyuuan.app.find_author.core.database.model.Image
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

/**
 * @author: guyuuan
 * @createTime: 6/6/24 17:50
 * @description:
 **/
sealed class ScanStatus<T> {
    data class Done<T>(val throwable: Throwable?=null) : ScanStatus<T>()
    class Idle<T> : ScanStatus<T>()
    data class Running<T>(val data: T) : ScanStatus<T>()
    data class Error<T>(val throwable: Throwable) : ScanStatus<T>()
}

interface MediaScanner : MediaStoreScanner, SAFMediaScanner, ShizukuMediaScanner {
    fun Flow<List<BucketItem>>.scamImages(
        update: suspend (image: ImageItem) -> Unit
    ): Flow<ScanStatus<BucketItem>>
}

class DefaultMediaScanner @Inject constructor(
    mediaStore: MediaStoreScanner, saf: SAFMediaScanner, shizuku: ShizukuMediaScanner
) : MediaScanner, MediaStoreScanner by mediaStore, SAFMediaScanner by saf,
    ShizukuMediaScanner by shizuku {
    override fun Flow<List<BucketItem>>.scamImages(
        update: suspend (image: ImageItem) -> Unit
    ): Flow<ScanStatus<BucketItem>> = transform { buckets ->
        for (bucket in buckets) {
            try {
                when (bucket.type) {
                    BucketType.MediaStore -> {
                        emit(ScanStatus.Running(bucket))
                        scanMediaStoreBucketImages(bucket.id).collect {
                            if (it is ScanStatus.Running) {
                                update(it.data)
                            }
                        }
                    }

                    BucketType.SAF -> {
                        emit(ScanStatus.Running(bucket))
                        check(bucket.uri != null) {
                            "bucket uri is null"
                        }
                        scanSAFBucketsImages(bucket.uri!!.toUri()).collect {
                            if (it is ScanStatus.Running) {
                                update(it.data.copy(bucketId = bucket.id))
                            }
                        }
                    }

                    BucketType.Shizuku -> emit(ScanStatus.Running(bucket))
                    else -> {}
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ScanStatus.Error(e))
            }
        }
    }.onCompletion {
        emit(ScanStatus.Done())
    }
}

