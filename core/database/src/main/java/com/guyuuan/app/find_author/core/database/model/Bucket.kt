package com.guyuuan.app.find_author.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

/**
 * @author: Chen
 * @createTime: 6/11/24 14:50
 * @description:
 **/
@Entity
data class Bucket(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val uri: String?,
    val relativePath: String,
    val selected: Boolean = false,
    val modifiedDate: Long,
    val coverUri: String?,
    val type: BucketType
)

sealed class BucketType {
    data object SAF : BucketType()
    data object MediaStore : BucketType()
    data object Root : BucketType()
    data object Shizuku : BucketType()
}

class BucketTypeConverter {
    @TypeConverter
    fun toType(type: BucketType) = when (type) {
        BucketType.SAF -> 0
        BucketType.MediaStore -> 1
        BucketType.Root -> 2
        BucketType.Shizuku -> 3
    }

    @TypeConverter
    fun fromType(type: Int) = when (type) {
        0 -> BucketType.SAF
        1 -> BucketType.MediaStore
        2 -> BucketType.Root
        3 -> BucketType.Shizuku
        else -> BucketType.SAF
    }
}