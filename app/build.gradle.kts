plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.pokeapitest"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.pokeapitest"
        minSdk = 24
        targetSdk = 36
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Retrofit
    implementation(libs.retrofit2.retrofit)
    implementation(libs.converter.gson)
    // Paging 3
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.paging.compose)
    // Coil (para cargar imágenes)
    implementation(libs.coil.compose)
    // Corrutinas
    implementation(libs.kotlinx.coroutines.android)
    // Dependencias para Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.foundation)
    ksp(libs.dagger.hilt.compiler)
    // Hilt con Compose
    implementation(libs.androidx.hilt.navigation.compose)
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation("androidx.room:room-paging:2.6.1")
    //Glide
    implementation("com.github.bumptech.glide:compose:1.0.0-alpha.1")
    //Google Play Services Location
    implementation("com.google.android.gms:play-services-location:21.0.1")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}