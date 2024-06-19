package com.guyuuan.app.find_author.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.guyuuan.app.find_author.core.database.model.Bucket
import kotlinx.coroutines.flow.Flow

/**
 * @author: Chen
 * @createTime: 6/11/24 14:52
 * @description:
 **/
@Dao
interface BucketDao {

    @Insert
    suspend fun insertBucket(vararg item: Bucket)

    @Query("SELECT * FROM bucket ORDER BY modifiedDate DESC")
    fun getBuckets(): Flow<List<Bucket>>

    @Query("SELECT * FROM bucket WHERE id = :bucketId")
    suspend fun getBucket(bucketId: Long): Bucket?

    @Delete
    suspend fun deleteBucket(vararg item: Bucket)

    @Update
    suspend fun updateBucket(vararg item: Bucket)
}