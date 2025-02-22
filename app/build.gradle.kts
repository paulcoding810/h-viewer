import org.jetbrains.kotlin.util.prefixIfNot

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            keyAlias = "androiddebugkey"
            storePassword = "android"
            keyPassword = "android"
        }
    }
    namespace = "com.paulcoding.hviewer"
    compileSdk = 35

    val repoUrl = providers.exec {
        commandLine = "git remote get-url origin".split(' ')
    }.standardOutput.asText.get().trim().removePrefix("https://github.com/")
        .removePrefix("git@github.com:")
        .removeSuffix(".git")
        .prefixIfNot("https://github.com/")

    defaultConfig {
        applicationId = "com.paulcoding.hviewer"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.4.1"

        buildConfigField("String", "REPO_URL", "\"$repoUrl\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        splits {
            abi {
                isEnable = true
                reset()
                include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                isUniversalApk = true
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            resValue("string", "app_name", "H Viewer (Dev)")
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
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

    implementation(libs.js)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.mmkv)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.gif)
    implementation(libs.coil.video)
    implementation(libs.ksoup.lite)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.javascriptengine)
    implementation(libs.androidx.concurrent.futures.ktx)
    implementation(libs.mmkv)
    implementation(libs.commons.compress)
    implementation(libs.lottie.compose)
    implementation(libs.zoomable)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.androidx.webkit)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation(platform(libs.bom))
    implementation(libs.editor)
    implementation(libs.language.textmate)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}