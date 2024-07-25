package com.guyuuan.app.find_author.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.guyuuan.app.find_author.core.data.model.BucketItem
import com.guyuuan.app.find_author.core.database.dao.BucketDao
import com.guyuuan.app.find_author.core.database.model.Bucket
import com.guyuuan.app.find_author.core.database.model.BucketType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @author: guyuuan
 * @createTime: 6/11/24 14:38
 * @description:
 **/
interface BucketRepository {
    suspend fun addBucket(vararg buckets: BucketItem)
    suspend fun deleteBucket(vararg buckets: BucketItem)
    suspend fun updateBucket(vararg buckets: BucketItem)
    fun getAllBuckets(): Flow<List<BucketItem>>
    fun getPagingBuckets(config: PagingConfig = PagingConfig(pageSize = 10)): Pager<Int, BucketItem>
    suspend fun getBucket(bucketId: Long): BucketItem?
    fun getSelectedBuckets(): Flow<List<BucketItem>>
    suspend fun getBucketType(bucketId: Long): BucketType
}

class DefaultBucketRepository @Inject constructor(
    private val bucketDao: BucketDao
) : BucketRepository {

    override suspend fun addBucket(vararg buckets: Bucket) = bucketDao.insertBucket(*buckets)

    override suspend fun deleteBucket(vararg buckets: Bucket) =
        bucketDao.deleteBucket(*buckets)

    override suspend fun updateBucket(vararg buckets: Bucket) =
        bucketDao.updateBucket(*buckets)

    override fun getAllBuckets() = bucketDao.getBuckets()

    override suspend fun getBucket(bucketId: Long) = bucketDao.getBucket(bucketId)

    override fun getSelectedBuckets() = bucketDao.getSelectedBuckets()
    override suspend fun getBucketType(bucketId: Long) = bucketDao.getBucketType(bucketId)


    override fun getPagingBuckets(config: PagingConfig) =
        Pager(config = config) {
            bucketDao.getPagingBuckets()
        }

}

