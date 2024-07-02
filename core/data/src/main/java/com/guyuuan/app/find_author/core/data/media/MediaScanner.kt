package com.guyuuan.app.find_author.core.data.media

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import com.guyuuan.app.find_author.core.data.model.BucketItem
import com.guyuuan.app.find_author.core.data.model.ImageItem
import com.guyuuan.app.find_author.core.database.model.BucketType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 6/6/24 17:50
 * @description:
 **/
sealed class ScanStatus<T> {
    class Done<T> : ScanStatus<T>()
    data class Running<T>(val data: T) : ScanStatus<T>()
    data class Error<T>(val throwable: Throwable) : ScanStatus<T>()
}

interface MediaScanner : MediaStoreScanner, SAFMediaScanner, ShizukuMediaScanner

class DefaultMediaScanner @Inject constructor(
    mediaStore: MediaStoreScanner, saf: SAFMediaScanner, shizuku: ShizukuMediaScanner
) : MediaScanner, MediaStoreScanner by mediaStore, SAFMediaScanner by saf,
    ShizukuMediaScanner by shizuku

