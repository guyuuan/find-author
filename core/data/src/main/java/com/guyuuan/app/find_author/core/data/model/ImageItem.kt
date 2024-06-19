package com.guyuuan.app.find_author.core.data.model

/**
 * @author: Chen
 * @createTime: 6/7/24 11:31
 * @description:
 **/
data class ImageItem(
    val id:Long,
    val name: String,
    val uri:String,
    val path:String,
    val dateAdded:Long,
    val bucketId:Long,
    val bucketName:String,
    val relativePath:String,
    val mimeType:String
)
