package com.guyuuan.app.find_author.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.guyuuan.app.find_author.core.database.model.Bucket
import com.guyuuan.app.find_author.core.database.model.BucketType
import kotlinx.coroutines.flow.Flow

/**
 * @author: guyuuan
 * @createTime: 6/11/24 14:52
 * @description:
 **/
@Dao
interface BucketDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBucket(vararg item: Bucket)

    @Query("SELECT * FROM bucket ORDER BY modifiedDate DESC")
    fun getBuckets(): Flow<List<Bucket>>

    @Query("SELECT * FROM bucket ORDER BY modifiedDate DESC")
    fun getPagingBuckets(): PagingSource<Int, Bucket>

    @Query("SELECT * FROM bucket WHERE id = :bucketId")
    suspend fun getBucket(bucketId: Long): Bucket?

    @Delete
    suspend fun deleteBucket(vararg item: Bucket)

    @Update
    suspend fun updateBucket(vararg item: Bucket)

    @Query("SELECT * FROM bucket WHERE selected = 1 ORDER BY modifiedDate DESC")
    fun getSelectedBuckets(): Flow<List<Bucket>>

    @Query("SELECT type FROM bucket WHERE id = :bucketId")
    suspend fun getBucketType(bucketId: Long): BucketType
}