package com.guyuuan.app.find_author.core.data.model

import android.content.ContentUris
import android.provider.MediaStore
import com.guyuuan.app.find_author.core.database.model.BucketType

/**
 * @author: Chen
 * @createTime: 6/11/24 09:17
 * @description:
 **/
data class BucketItem(
    val id: Long,
    val name: String,
    val uri:String?=null,
    val relativePath: String,
    val selected: Boolean = false,
    val modifiedDate:Long,
    val coverUri:String?,
    val type:BucketType
)