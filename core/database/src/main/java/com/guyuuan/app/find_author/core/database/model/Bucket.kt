package com.guyuuan.app.find_author.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author: Chen
 * @createTime: 6/11/24 14:50
 * @description:
 **/
@Entity
data class Bucket(
    @PrimaryKey val id: Long,
    val name: String,
    val relativePath: String,
    val selected:Boolean,
    val coverId:Long?,
    val modifiedDate:Long
)