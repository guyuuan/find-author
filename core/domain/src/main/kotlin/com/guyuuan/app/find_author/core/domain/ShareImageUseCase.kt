package com.guyuuan.app.find_author.core.domain

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.guyuuan.app.find_author.core.common.R
import com.guyuuan.app.find_author.core.data.BucketRepository
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.database.model.BucketType
import java.io.File
import javax.inject.Inject

/**
 * @author: guyuuan
 * @createTime: 7/25/24 10:41
 * @description:
 **/
class ShareImageUseCase @Inject constructor(
    private val bucketRepository: BucketRepository
) {
    suspend operator fun invoke(image: ImageItem, context: Context) {
        val bucket =
            bucketRepository.getBucket(image.bucketId) ?: throw RuntimeException("bucket not found")
        val bucketType = bucket.type
        val file = context.contentResolver.openInputStream(image.uri.toUri())?.use {
            val cacheDir = context.externalCacheDir ?: context.cacheDir
            val file = File(cacheDir, "images${File.separatorChar}${bucket.name}${File.separatorChar}${image.name}")
            val out = file.outputStream()
            it.copyTo(out)
            out.close()
            file
        }
        if (file == null) {
            return
        }
        val shareUri = FileProvider.getUriForFile(context, "${context.packageName}.share", file)
        if (bucketType == BucketType.MediaStore) {
            context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, shareUri)
                type = "image/*"
            }, context.getString(R.string.share_title)))
        } else if (bucketType == BucketType.SAF) {

        }
    }
}