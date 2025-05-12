plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.undercouch.download)
}

android {
    namespace = "com.lucasprioste.mediapipeandroid"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lucasprioste.mediapipeandroid"
        minSdk = 26
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
    }
    sourceSets {
        getByName("main").assets.srcDirs("src/main/assets")
    }
}

dependencies {
    // UI
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    // CameraX
    implementation(libs.bundles.camerax)

    // MediaPipe
    implementation(libs.mediapipe.tasks.vision)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Navigation
    implementation(libs.androidx.compose.navigation)

    // Koin
    implementation(libs.koin.android)

    // Permissions
    implementation(libs.accompanist.permissions)
}

val assetsDir = "src/main/assets"
val objectDetectionName = "object_detection.tflite"

tasks.register("createAssetsDirectory") {
    description = "Create assets directory if it doesn't exist"
    group = "models"
    doLast {
        val directory = file(assetsDir)
        if (!directory.exists()) {
            directory.mkdirs()
            println("Created assets directory at ${directory.absolutePath}")
        }
    }
}

tasks.register<de.undercouch.gradle.tasks.download.Download>("downloadTFLiteModel") {
    description = "Download TFLite model for object detection"
    group = "models"

    dependsOn("createAssetsDirectory")

    src("https://storage.googleapis.com/mediapipe-models/object_detector/efficientdet_lite0/float32/1/efficientdet_lite0.tflite")
    dest(file("$assetsDir/$objectDetectionName"))
    overwrite(false) // Don't download if the file already exists

    doLast {
        println("Downloaded TFLite model to ${file("$assetsDir/$objectDetectionName").absolutePath}")
    }
}

tasks.named("preBuild") {
    dependsOn("downloadTFLiteModel")
}