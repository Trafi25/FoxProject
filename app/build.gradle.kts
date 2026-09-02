plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.traffipart.foxapplication"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.traffipart.foxapplication"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    testImplementation(libs.junit)
}