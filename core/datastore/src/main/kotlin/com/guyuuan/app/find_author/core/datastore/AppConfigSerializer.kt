package com.guyuuan.app.find_author.core.datastore

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 6/24/24 15:11
 * @description:
 **/
class AppConfigSerializer @Inject constructor() : Serializer<AppConfig> {
    override val defaultValue: AppConfig
        get() = AppConfig.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AppConfig {
        return AppConfig.parseFrom(input)
    }

    override suspend fun writeTo(t: AppConfig, output: OutputStream) {
        t.writeTo(output)
    }
}