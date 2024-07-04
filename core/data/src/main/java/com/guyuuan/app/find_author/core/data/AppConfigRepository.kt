package com.guyuuan.app.find_author.core.data

import androidx.datastore.core.DataStore
import com.guyuuan.app.find_author.core.datastore.proto.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 6/24/24 15:37
 * @description:
 **/
interface AppConfigRepository {
    fun getInitializedFlow(): Flow<Boolean>

    suspend fun setInitialized()
}

class DefaultAppConfigRepository @Inject constructor(
    private val dataStore: DataStore<AppConfig>
) : AppConfigRepository {
    override fun getInitializedFlow(): Flow<Boolean> =
        dataStore.data.map { it.initialized }

    override suspend fun setInitialized() {
        dataStore.updateData { current ->
            current.toBuilder().setInitialized(true).build()
        }
    }
}

class FakeAppConfigRepository @Inject constructor() : AppConfigRepository {
    override fun getInitializedFlow(): Flow<Boolean> = flow {
        emit(false)
    }

    override suspend fun setInitialized() {
    }
}