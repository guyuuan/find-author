package com.guyuuan.app.find_author.core.data

import com.guyuuan.app.find_author.core.data.media.MediaStoreScanner
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 6/11/24 15:31
 * @description:
 **/
interface MediaRepository : BucketRepository, ImageRepository {
    suspend fun loadSystemBuckets()
}

class DefaultMediaRepository @Inject constructor(
    private val bucketRepository: BucketRepository,
    private val imageRepository: ImageRepository,
    private val scanner: MediaStoreScanner
) : MediaRepository, BucketRepository by bucketRepository, ImageRepository by imageRepository {
    override suspend fun loadSystemBuckets() {
        scanner.scanBuckets().collect {
            val buckets = getBucket(it.id)
            if (buckets == null) {
                addBucket(it)
            } else {
                updateBucket(it.copy(selected = buckets.selected))
            }
        }
    }
}