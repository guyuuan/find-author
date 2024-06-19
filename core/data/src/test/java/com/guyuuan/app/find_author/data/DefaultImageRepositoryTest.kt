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

package com.guyuuan.app.find_author.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.guyuuan.app.find_author.core.data.DefaultImageRepository
import com.guyuuan.app.find_author.core.database.Home
import com.guyuuan.app.find_author.core.database.HomeDao

/**
 * Unit tests for [DefaultImageRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class DefaultImageRepositoryTest {

    @Test
    fun homes_newItemSaved_itemIsReturned() = runTest {
        val repository = DefaultImageRepository(FakeHomeDao())

        repository.add("Repository")

        assertEquals(repository.homes.first().size, 1)
    }

}

private class FakeHomeDao : HomeDao {

    private val data = mutableListOf<Home>()

    override fun getHomes(): Flow<List<Home>> = flow {
        emit(data)
    }

    override suspend fun insertHome(item: Home) {
        data.add(0, item)
    }
}
