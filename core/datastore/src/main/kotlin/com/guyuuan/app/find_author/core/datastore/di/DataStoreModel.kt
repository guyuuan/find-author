package com.guyuuan.app.find_author.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.guyuuan.app.find_author.core.datastore.proto.AppConfig
import com.guyuuan.app.find_author.core.datastore.AppConfigSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import androidx.datastore.dataStoreFile
/**
 * @author: Chen
 * @createTime: 6/24/24 15:28
 * @description:
 **/
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModel {

    @Provides
    fun provideAppConfigDataStore(
        @ApplicationContext context: Context,
        serializer: AppConfigSerializer
    ):DataStore<AppConfig> = DataStoreFactory.create(
        serializer = serializer,
    ){
        context.dataStoreFile("app_config.pb")
    }
}