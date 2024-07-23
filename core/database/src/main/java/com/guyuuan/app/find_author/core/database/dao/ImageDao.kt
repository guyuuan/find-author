package com.guyuuan.app.find_author.core.database.dao

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.guyuuan.app.find_author.core.database.model.Image
import kotlinx.coroutines.flow.Flow

/**
 * @author: guyuuan
 * @createTime: 6/11/24 14:49
 * @description:
 **/
@Dao
interface ImageDao {
    @Query("SELECT * FROM image ORDER BY dateAdded DESC")
    fun getImages(): Flow<List<Image>>

    @Query("SELECT * FROM image WHERE id = :id")
   suspend fun getImageById(id:String):Image?

    @Insert()
    suspend fun insertImage( vararg item: Image)

    @Update
    suspend fun updateImage(vararg item: Image)

    @Query("SELECT * FROM image WHERE bucketId = :bucketId ORDER BY dateAdded DESC")
    fun getBuketImages(bucketId: Long): PagingSource<Int,Image>

    @Query("DELETE FROM image WHERE bucketId = :bucketId")
    suspend fun deleteBucketImage(bucketId: Long)

    @Query("SELECT * FROM image WHERE bucketId = :bucketId ORDER BY dateAdded DESC LIMIT 1")
    fun getBuketCover(bucketId: Long): Image?

    @Query("SELECT image.* FROM image INNER JOIN bucket ON image.bucketId = bucket.id WHERE (bucket.hide = :showHide OR bucket.hide =0 ) AND bucket.selected = 1 ORDER BY dateAdded DESC")
    fun getHomeImages(showHide:Boolean): PagingSource<Int, Image>
}