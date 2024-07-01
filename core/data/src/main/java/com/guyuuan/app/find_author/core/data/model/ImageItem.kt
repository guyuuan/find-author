package com.guyuuan.app.find_author.core.data.model

/**
 * @author: Chen
 * @createTime: 6/7/24 11:31
 * @description:
 **/
data class ImageItem(
    val id: String,
    val name: String,
    val uri: String,
    val path: String? = null,
    val dateAdded: Long,
    val bucketId: Long,
    val bucketName: String? = null,
    val relativePath: String? = null,
    val mimeType: String
)
