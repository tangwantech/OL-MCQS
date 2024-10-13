plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.gceolmcqs"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gceolmcqs"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    val room_version = "2.6.0"
//    implementation (libs.androidx.room.runtime)
//    kapt(libs.androidx.room.compiler)
//    implementation (libs.androidx.room.ktx)
    implementation(libs.parse)
    implementation(libs.androidx.cardview)
    implementation (libs.android.sdk)
    implementation (libs.rxjava)
    implementation(libs.coroutine)
    implementation(libs.liveData)
    implementation(libs.gson)
    implementation(libs.viewModel)
    implementation(libs.okhttp3)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}