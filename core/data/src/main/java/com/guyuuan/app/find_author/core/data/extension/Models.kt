package com.guyuuan.app.find_author.core.data.extension

import com.guyuuan.app.find_author.core.data.model.BucketItem
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.database.model.Bucket
import com.guyuuan.app.find_author.core.database.model.Image

/**
 * @author: Chen
 * @createTime: 6/11/24 15:26
 * @description:
 **/

fun Image.toImageItem() = ImageItem(
    id, name, uri, path, dateAdded, bucketId, bucketName, relativePath, mimeType
)

fun ImageItem.toImage() = Image(
    id, name, uri, path, dateAdded, bucketId, bucketName, relativePath, mimeType
)

fun Bucket.toBucketItem() =
    BucketItem(id, name, uri, relativePath, selected, modifiedDate, coverUri, type)

fun BucketItem.toBucket() =
    Bucket(id, name, uri, relativePath, selected, modifiedDate, coverUri, type)