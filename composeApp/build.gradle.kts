@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.kotlinCocoapods)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }

        iosTarget.compilations["main"].cinterops {
            create("FirebaseKMPWrapper") {
                defFile(project.file("src/nativeInterop/cinterop/firebase/FirebaseKMPWrapper.def"))
                @Suppress("DEPRECATION")
                packageName("org.nla.phototovideoai.composeapp.nativeInterop.cinterop.firebase")
            }
        }
        // Remove unnecessary klib dependencies from the cinterop task
        project.afterEvaluate {
            val targetName = iosTarget.name.replaceFirstChar { it.uppercaseChar() }
            tasks.findByName("cinteropFirebaseKMPWrapper$targetName")?.let { task ->
                if (task is org.jetbrains.kotlin.gradle.tasks.CInteropProcess) {
                    task.settings.dependencyFiles = files()
                }
            }
        }
    }

    cocoapods {
        version = "1.16.2"
        summary = "CocoaPods library"
        homepage = "https://github.com/JetBrains/kotlin"
        ios.deploymentTarget = "15.0"
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.koin.android)
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.androidx.browser)
            implementation("io.github.parksanggwon:tedimagepicker:1.7.3") {
                exclude(group = "com.android.support")
            }

            //AppMetrica
            implementation(libs.appmetrica.analytics)

            //AppMetrica Push SDK
            implementation(libs.appmetrica.push)
            implementation(libs.appmetrica.push.provider.firebase)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.bundles.koin)
            implementation(libs.okio)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.kotlinx.datetime)
            implementation(libs.sqldelight.coroutines.extensions)

            implementation(libs.bundles.moko.media)
            implementation(project(":cropper"))
            implementation(project(":vkauthdonate"))

            implementation(libs.bundles.coil)

            implementation(libs.crashlytics)

            //RevCat official
            implementation(libs.bundles.revcat.purchases)

            implementation(libs.compose.multiplatform.media.player)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.stately.common)
            implementation(libs.sqldelight.native.driver)
        }
    }

    sqldelight {
        databases {
            create("AppDatabase") {
                packageName.set("org.nla.phototovideoai")
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "phototovideoai.composeapp.generated.resources"
    generateResClass = auto
}

android {
    namespace = "org.nla.phototovideoai"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.nla.phototovideoai"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "boolean",
            "IS_RUSTORE",
            "true" //true //false
        )

        addManifestPlaceholders(
            mapOf(
                "VKIDClientID" to "54407615",
                "VKIDClientSecret" to "Wr1bFFDfySBoSrl6RWN1",
                "VKIDRedirectHost" to "vk.ru",
                "VKIDRedirectScheme" to "vk54407615"
            )
        )
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    dependencies {

        //Firebase
        implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
        implementation("com.google.firebase:firebase-analytics")
        implementation("com.google.firebase:firebase-crashlytics")
        implementation("com.google.firebase:firebase-config")
        implementation("com.google.firebase:firebase-messaging")
        implementation("com.google.firebase:firebase-inappmessaging-display")
        implementation("com.google.android.gms:play-services-base:18.3.0")

        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")

        implementation(platform("ru.rustore.sdk:bom:2025.08.01"))
        implementation("ru.rustore.sdk:pay")

        implementation("com.android.billingclient:billing-ktx:8.0.0")

        implementation(libs.androidx.media3.exoplayer)
        implementation(libs.androidx.media3.ui.compose)
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}