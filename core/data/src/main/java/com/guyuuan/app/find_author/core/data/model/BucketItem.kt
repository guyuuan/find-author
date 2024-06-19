package com.guyuuan.app.find_author.core.data.model

import android.content.ContentUris
import android.provider.MediaStore

/**
 * @author: Chen
 * @createTime: 6/11/24 09:17
 * @description:
 **/
data class BucketItem(
    val id: Long,
    val name: String,
    val relativePath: String,
    val coverId: Long?,
    val selected: Boolean = false,
    val modifiedDate:Long
) {
    val coverUri = coverId?.let {
        ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it
        )
    }
}
