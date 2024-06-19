package com.guyuuan.app.find_author.core.database.dao

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guyuuan.app.find_author.core.database.model.Image
import kotlinx.coroutines.flow.Flow

/**
 * @author: Chen
 * @createTime: 6/11/24 14:49
 * @description:
 **/
@Dao
interface ImageDao {
    @Query("SELECT * FROM image ORDER BY dateAdded DESC")
    fun getImages(): Flow<List<Image>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage( vararg item: Image)

    @Query("SELECT * FROM image WHERE bucketId = :bucketId ORDER BY dateAdded DESC")
    fun getBuketImages(bucketId: Long): PagingSource<Int,Image>

    @Query("DELETE FROM image WHERE bucketId = :bucketId")
    suspend fun deleteBucketImage(bucketId: Long)

    @Query("SELECT * FROM image WHERE bucketId = :bucketId ORDER BY dateAdded DESC LIMIT 1")
    fun getBuketCover(bucketId: Long): Image?
}