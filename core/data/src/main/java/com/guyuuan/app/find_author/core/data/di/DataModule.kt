/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guyuuan.app.find_author.core.data.di

import com.guyuuan.app.find_author.core.data.AppConfigRepository
import com.guyuuan.app.find_author.core.data.BucketRepository
import com.guyuuan.app.find_author.core.data.DefaultAppConfigRepository
import com.guyuuan.app.find_author.core.data.DefaultBucketRepository
import com.guyuuan.app.find_author.core.data.DefaultImageRepository
import com.guyuuan.app.find_author.core.data.DefaultMediaRepository
import com.guyuuan.app.find_author.core.data.FakeAppConfigRepository
import com.guyuuan.app.find_author.core.data.ImageRepository
import com.guyuuan.app.find_author.core.data.MediaRepository
import com.guyuuan.app.find_author.core.data.media.AndroidMediaStoreScanner
import com.guyuuan.app.find_author.core.data.media.DefaultMediaScanner
import com.guyuuan.app.find_author.core.data.media.MediaScanner
import com.guyuuan.app.find_author.core.data.media.MediaStoreScanner
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsHomeRepository(
        homeRepository: DefaultImageRepository
    ): ImageRepository

    @Singleton
    @Binds
    fun bindsMediaScanner(
        mediaScanner: DefaultMediaScanner
    ): MediaScanner

    @Singleton
    @Binds
    fun bindsBucketRepository(
        bucketRepository: DefaultBucketRepository
    ): BucketRepository

    @Singleton
    @Binds
    fun bindsMediaRepository(
        mediaRepository: DefaultMediaRepository
    ): MediaRepository

    @Singleton
    @Binds
    fun bindAppConfigRepository(
        appConfigRepository: DefaultAppConfigRepository
    ): AppConfigRepository
}

//class FakeHomeRepository @Inject constructor() : HomeRepository {
//    override val homes: Flow<List<String>> = flowOf(fakeHomes)
//
//    override suspend fun add(name: String) {
//        throw NotImplementedError()
//    }
//}
//
//val fakeHomes = listOf("One", "Two", "Three")
