plugins {
    id("com.android.application")
}

android {
    namespace = "com.nuvio.bridge"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nuvio.bridge"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
}

dependencies {
    implementation("io.github.dokar3:quickjs-kt:1.0.5")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
}
