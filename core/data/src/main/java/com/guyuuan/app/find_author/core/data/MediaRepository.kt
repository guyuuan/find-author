package com.guyuuan.app.find_author.core.data

import com.guyuuan.app.find_author.core.data.media.MediaScanner
import com.guyuuan.app.find_author.core.data.media.ScanStatus
import com.guyuuan.app.find_author.core.database.model.Bucket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

/**
 * @author: guyuuan
 * @createTime: 6/11/24 15:31
 * @description:
 **/
interface MediaRepository : BucketRepository, ImageRepository {
    suspend fun loadSystemBuckets()

    fun loadBucketsImages(coroutineScope: CoroutineScope): Flow<ScanStatus<Bucket>>
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

    override fun loadBucketsImages(coroutineScope: CoroutineScope) = with(scanner) {
        val buckets = getSelectedBuckets().shareIn(
            coroutineScope, SharingStarted.WhileSubscribed(), replay = 1
        )
        val scan = buckets.scamImages {
            checkImageShouldUpdate(it)
        }
        val check = buckets.checkImagesExist().stateIn(coroutineScope, SharingStarted.Eagerly, Unit)
        scan.flowOn(Dispatchers.IO)
    }


}