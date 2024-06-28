package com.guyuuan.app.find_author.core.data

import com.guyuuan.app.find_author.core.data.extension.toBucket
import com.guyuuan.app.find_author.core.data.extension.toBucketItem
import com.guyuuan.app.find_author.core.data.model.BucketItem
import com.guyuuan.app.find_author.core.database.dao.BucketDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 6/11/24 14:38
 * @description:
 **/
interface BucketRepository {
    suspend fun addBucket(vararg bucket: BucketItem)
    suspend fun deleteBucket(vararg bucket: BucketItem)
    suspend fun updateBucket(vararg bucket: BucketItem)
    fun getAllBuckets(): Flow<List<BucketItem>>
    suspend fun getBucket(bucketId: Long): BucketItem?
    fun getSelectedBuckets(): Flow<List<BucketItem>>
}

class DefaultBucketRepository @Inject constructor(
    private val bucketDao: BucketDao
) : BucketRepository {

    override suspend fun addBucket(vararg bucket: BucketItem) = bucketDao.insertBucket(*bucket.map {
        it.toBucket()
    }.toTypedArray())

    override suspend fun deleteBucket(vararg bucket: BucketItem) =
        bucketDao.deleteBucket(*bucket.map {
            it.toBucket()
        }.toTypedArray())

    override suspend fun updateBucket(vararg bucket: BucketItem) =
        bucketDao.updateBucket(*bucket.map {
            it.toBucket()
        }.toTypedArray())

    override fun getAllBuckets() = bucketDao.getBuckets().map { buckets ->
        buckets.map {
            it.toBucketItem()
        }
    }

    override suspend fun getBucket(bucketId: Long) =
        bucketDao.getBucket(bucketId)?.toBucketItem()

    override fun getSelectedBuckets() = bucketDao.getSelectedBuckets().map { buckets ->
        buckets.map {
            it.toBucketItem()
        }
    }
}

