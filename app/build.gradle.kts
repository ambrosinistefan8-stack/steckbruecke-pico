plugins {
    id("com.android.application") version "8.8.0"
    id("org.jetbrains.kotlin.android") version "2.0.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
}

android {
    namespace = "ch.spinnennetz.steckbruecke"
    compileSdk = 35
    defaultConfig {
        applicationId = "ch.spinnennetz.steckbruecke"
        minSdk = 35
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
        ndk { abiFilters.add("arm64-v8a") }
    }
    buildFeatures { compose = true }
    androidResources { noCompress += "glb" }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
}

dependencies {
    implementation(platform("com.pico.spatial:bom:6.2.8"))
    implementation("com.pico.spatial.core:core")
    implementation("com.pico.spatial.foundation:foundation")
    implementation("com.pico.spatial.tracking:tracking")
    implementation("com.pico.spatial.sense:sense")
    implementation("com.pico.spatial.ui:platform")
    implementation("com.pico.spatial.ui:foundation")
    implementation("com.pico.spatial.ui:design")
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
}

configurations.all {
    exclude(group = "androidx.compose.ui", module = "ui")
    exclude(group = "androidx.compose.ui", module = "ui-graphics")
    exclude(group = "androidx.compose.ui", module = "ui-text")
    exclude(group = "androidx.compose.foundation", module = "foundation")
}
