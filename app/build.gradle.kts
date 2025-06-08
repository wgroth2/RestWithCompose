plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.digiroth.restwithcompose"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.digiroth.restwithcompose"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)

    // Jetpack DataStore (Preferences)
    implementation(libs.androidx.datastore.preferences)

// For ViewModel integration with collectAsStateWithLifecycle
    implementation(libs.androidx.lifecycle.runtime.compose) // or newer
    implementation(libs.lifecycle.viewmodel.compose) // or newer
    // Retrofit for networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Or latest version
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Gson converter

// Gson for JSON parsing (Retrofit's Gson converter will bring this in, but explicit can be good)
    implementation("com.google.code.gson:gson:2.11.0") // Or latest version

// OkHttp Logging Interceptor (optional, but very useful for debugging network calls)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("androidx.preference:preference-ktx:1.2.1") // Or the latest version
    // For Networking (OkHttp)
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // Or latest

    // For Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // Or latest
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity) // Or latest compatible with Retrofit's OkHttp

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}