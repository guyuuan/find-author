package com.guyuuan.app.find_author.core.data

import com.guyuuan.app.find_author.core.data.media.MediaScanner
import com.guyuuan.app.find_author.core.data.media.ScanStatus
import com.guyuuan.app.find_author.core.data.model.BucketItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 6/11/24 15:31
 * @description:
 **/
interface MediaRepository : BucketRepository, ImageRepository {
    suspend fun loadSystemBuckets()

    fun loadBucketsImages(): Flow<ScanStatus<BucketItem>>
}

class DefaultMediaRepository @Inject constructor(
    private val bucketRepository: BucketRepository,
    private val imageRepository: ImageRepository,
    private val scanner: MediaScanner
) : MediaRepository, BucketRepository by bucketRepository, ImageRepository by imageRepository {
    override suspend fun loadSystemBuckets() {
        scanner.scanMediaStoreBuckets().collect {
            val buckets = getBucket(it.id)
            if (buckets == null) {
                addBucket(it)
            } else {
                updateBucket(it.copy(selected = buckets.selected))
            }
        }
    }

    override fun loadBucketsImages() = with(scanner) {
        getSelectedBuckets().scamImages {
            checkImageShouldUpdate(it)
        }.flowOn(Dispatchers.IO)
    }


}