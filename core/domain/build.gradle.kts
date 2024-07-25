plugins{
    alias(libs.plugins.guyuuan.android.lib)
    alias(libs.plugins.guyuuan.android.hilt)
}

android {
    namespace = "com.guyuuan.app.find_author.core.domain"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:common"))
    implementation(libs.androidx.core.ktx)
}