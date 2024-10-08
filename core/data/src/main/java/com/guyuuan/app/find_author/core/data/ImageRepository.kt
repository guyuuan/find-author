/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guyuuan.app.find_author.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.data.model.PagingUiMode
import com.guyuuan.app.find_author.core.data.util.asLocalDateTime
import com.guyuuan.app.find_author.core.data.util.formatedDateString
import com.guyuuan.app.find_author.core.database.dao.ImageDao
import com.guyuuan.app.find_author.core.database.model.Image
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ImageRepository {

    fun getAllImages(): Flow<List<Image>>

    suspend fun checkImageShouldUpdate(image: Image)

    suspend fun updateImage(vararg image: Image)

    fun getImagesPagerByBucket(
        bucketId: Long, config: PagingConfig = PagingConfig(pageSize = 30)
    ): Pager<Int, ImageItem>

    fun getHomeImages(
        showHide: Boolean = false, config: PagingConfig = PagingConfig(pageSize = 30)
    ): Flow<PagingData<PagingUiMode<ImageItem>>>

    fun getImages(
        showHide: Boolean = false, config: PagingConfig = PagingConfig(pageSize = 10)
    ): Flow<PagingData<ImageItem>>

    fun getBucketCover(bucketId: Long): Image?

    suspend fun addImage(vararg image: Image)

    suspend fun deleteImageByBucket(bucketId: Long)

}

class DefaultImageRepository @Inject constructor(
    private val imageDao: ImageDao
) : ImageRepository {

    override fun getAllImages() = imageDao.getImages()

    override fun getImagesPagerByBucket(bucketId: Long, config: PagingConfig) = Pager(config = config) {
        imageDao.getBuketImagesPager(bucketId)
    }

    override fun getHomeImages(
        showHide: Boolean, config: PagingConfig
    ) = Pager(config) {
        imageDao.getHomeImages(showHide)
    }.flow.map { pagingData ->
        var index = 0
        pagingData.map<ImageItem, PagingUiMode<ImageItem>> {
            PagingUiMode.Item(it,index++)
        }.insertSeparators { before, after ->
                if (after != null && after is PagingUiMode.Item) {
                    val afterTime = after.data.dateAdded.asLocalDateTime
                    val beforeTime = before?.data?.dateAdded?.asLocalDateTime
                        ?: return@insertSeparators PagingUiMode.Header(afterTime.formatedDateString)
                    if (afterTime.dayOfMonth != beforeTime.dayOfMonth) {
                        PagingUiMode.Header(afterTime.formatedDateString)
                    } else {
                        null
                    }

                } else {
                    null
                }
            }
    }

    override fun getImages(
        showHide: Boolean, config: PagingConfig
    ) = Pager(config) {
        imageDao.getHomeImages(showHide)
    }.flow


    override fun getBucketCover(bucketId: Long) = imageDao.getBuketCover(bucketId)

    override suspend fun addImage(vararg image: Image) = imageDao.insertImage(*image)

    override suspend fun deleteImageByBucket(bucketId: Long) = imageDao.deleteBucketImage(bucketId)

    override suspend fun checkImageShouldUpdate(image: Image) {
        val img = imageDao.getImageById(image.id)
        if (img == null) {
            addImage(image)
            return
        }
        if (img != image) {
            updateImage(image)
        }
    }

    override suspend fun updateImage(vararg image: Image) {
        imageDao.updateImage(*image)
    }
}
