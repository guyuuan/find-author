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

@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.guyuuan.app.find_author.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.guyuuan.app.find_author.ui.screen.home.HomeViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ImageDetailScreenDestination
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.destination

@Composable
fun MainNavigation(modifier: Modifier) {
    SharedTransitionLayout(modifier=modifier) {
        DestinationsNavHost(NavGraphs.root, modifier = Modifier.fillMaxSize(), dependenciesContainerBuilder = {
            dependency(this@SharedTransitionLayout)
            destination(HomeScreenDestination) {
                dependency(hiltViewModel<HomeViewModel>(navBackStackEntry))
            }
            destination(ImageDetailScreenDestination) {
                dependency(hiltViewModel<HomeViewModel>(navController.getBackStackEntry(destination.route)))
            }
        })
    }
}
