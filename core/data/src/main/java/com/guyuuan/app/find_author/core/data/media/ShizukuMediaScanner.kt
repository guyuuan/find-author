package com.guyuuan.app.find_author.core.data.media

import android.net.Uri
import com.guyuuan.app.find_author.core.data.model.ImageItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * @author: guyuuan
 * @createTime: 7/2/24 16:38
 * @description:
 **/

interface ShizukuMediaScanner {
    fun scanShizukuBucketsImages(uri: Uri): Flow<ImageItem>
}


class DefaultShizukuMediaScanner @Inject constructor() : ShizukuMediaScanner {
    override fun scanShizukuBucketsImages(uri: Uri) = flow<ImageItem> { }
}
