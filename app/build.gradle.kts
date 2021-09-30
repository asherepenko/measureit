import com.github.triplet.gradle.androidpublisher.ReleaseStatus
import java.io.FileInputStream
import java.util.Properties
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("com.android.application")
    id("com.github.triplet.play") version "3.6.0"
    id("com.sherepenko.gradle.plugin-build-version") version "0.2.3"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    kotlin("android")
    kotlin("kapt")
}

val archivesBaseName = "measureit"

val keystorePropertiesFile = rootProject.file("keystore.properties")
val playstorePropertiesFile = rootProject.file("playstore.properties")

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 21
        targetSdk = 30
        applicationId = "com.sherepenko.android.measureit"
        versionCode = buildVersion.versionCode
        versionName = buildVersion.versionName
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        setProperty("archivesBaseName", "$archivesBaseName-$versionName")
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    lint {
        isCheckDependencies = true
        ignore("InvalidPackage")
    }

    packagingOptions {
        exclude("META-INF/LICENSE")
        exclude("META-INF/NOTICE")
    }

    testOptions {
        unitTests.apply {
            isIncludeAndroidResources = true
        }
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties().apply {
                    load(FileInputStream(keystorePropertiesFile))
                }

                storeFile = rootProject.file(keystoreProperties.getProperty("keystore.upload.file"))
                storePassword = keystoreProperties.getProperty("keystore.upload.password")
                keyAlias = keystoreProperties.getProperty("keystore.upload.key.alias")
                keyPassword = keystoreProperties.getProperty("keystore.upload.key.password")
            } else if (!System.getenv("KEYSTORE_FILE").isNullOrEmpty()) {
                storeFile = rootProject.file(System.getenv("KEYSTORE_FILE"))
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEYSTORE_KEY_ALIAS")
                keyPassword = System.getenv("KEYSTORE_KEY_PASSWORD")
            } else {
                val debugSigningConfig = getByName("debug")

                storeFile = debugSigningConfig.storeFile
                storePassword = debugSigningConfig.storePassword
                keyAlias = debugSigningConfig.keyAlias
                keyPassword = debugSigningConfig.keyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

ktlint {
    verbose.set(true)
    android.set(true)

    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
    }
}

play {
    if (playstorePropertiesFile.exists()) {
        val playstoreProperties = Properties().apply {
            load(FileInputStream(playstorePropertiesFile))
        }

        serviceAccountCredentials.set(
            rootProject.file(playstoreProperties.getProperty("playstore.credentials"))
        )
        defaultToAppBundles.set(true)
        track.set("alpha")
        releaseStatus.set(ReleaseStatus.IN_PROGRESS)
    } else if (!System.getenv("PLAYSTORE_CREDENTIALS").isNullOrEmpty()) {
        serviceAccountCredentials.set(
            rootProject.file(System.getenv("PLAYSTORE_CREDENTIALS"))
        )
        defaultToAppBundles.set(true)
        track.set("alpha")
        releaseStatus.set(ReleaseStatus.IN_PROGRESS)
    } else {
        enabled.set(false)
    }
}

val koinVersion = "3.1.2"
val lifecycleVersion = "2.3.1"
val roomVersion = "2.3.0"

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation(platform("com.google.firebase:firebase-bom:28.4.1"))
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.collection:collection-ktx:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.1")
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.fragment:fragment-ktx:1.3.6")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxjava:3.1.1")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("com.jakewharton.timber:timber:5.0.1")
}

apply(plugin = "com.google.gms.google-services")
apply(plugin = "com.google.firebase.crashlytics")
apply(plugin = "com.google.firebase.firebase-perf")
apply(plugin = "koin")
