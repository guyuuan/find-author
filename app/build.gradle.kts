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

@Suppress("DSL_SCOPE_VIOLATION") // Remove when fixed https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.guyuuan.android.app.compose)
    alias(libs.plugins.guyuuan.android.app)
    alias(libs.plugins.guyuuan.android.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.guyuuan.app.find_author"

    defaultConfig {
        applicationId = "com.guyuuan.app.find_author"
        vectorDrawables {
            useSupportLibrary = true
        }
        // Enable room auto-migrations
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    buildTypes {
        val debug by getting {
            try {
                signingConfig = signingConfigs.getByName("chitanda")
            } catch (_: Throwable) {
            }
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        val release by getting {
            isMinifyEnabled = true
            try {
                signingConfig = signingConfigs.getByName("chitanda")
            } catch (_: Throwable) {

            }
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        aidl = false
        buildConfig = false
        renderScript = false
        shaders = false
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
//
//    // Hilt Dependency Injection
//    implementation(libs.hilt.android)
//    kapt(libs.hilt.compiler)
//
//    // Arch Components
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
//
//    // Compose
//    val composeBom = platform(libs.androidx.compose.bom)
//    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.compose.destinations.core)

//    // Tooling
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.timber)
    implementation(libs.coil.kt)
    implementation(libs.coil.compose)
    implementation(libs.coil.kt.gif)
    implementation(libs.accompanist.permissions)
    ksp(libs.compose.destinations.ksp)
    implementation(libs.chitanda.dynamicstatusbar)
    implementation(libs.androidx.paging3.compose)
}
