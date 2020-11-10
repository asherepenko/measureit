import com.github.triplet.gradle.androidpublisher.ReleaseStatus
import java.io.FileInputStream
import java.util.Properties
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("com.android.application")
    id("com.github.triplet.play") version "3.0.0"
    id("com.sherepenko.gradle.plugin-build-version") version "0.2.1"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.0"
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

val archivesBaseName = "measureit"

val keystorePropertiesFile = rootProject.file("keystore.properties")
val playstorePropertiesFile = rootProject.file("playstore.properties")

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
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

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    lintOptions {
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
        getByName("release") {
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

val koinVersion = "2.2.0-rc-4"
val lifecycleVersion = "2.2.0"
val roomVersion = "2.2.5"

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.0")
    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycleVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.collection:collection-ktx:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.fragment:fragment-ktx:1.2.5")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.google.android.material:material:1.3.0-alpha03")
    implementation("com.google.firebase:firebase-analytics-ktx:18.0.0")
    implementation("com.google.firebase:firebase-crashlytics-ktx:17.2.2")
    implementation("com.google.firebase:firebase-perf:19.0.9")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxjava:3.0.1")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.0")
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("org.koin:koin-androidx-ext:$koinVersion")
    implementation("org.koin:koin-androidx-scope:$koinVersion")
    implementation("org.koin:koin-androidx-viewmodel:$koinVersion")
    testImplementation("junit:junit:4.13")
    testImplementation("androidx.test:core:1.3.0")
    testImplementation("androidx.test:runner:1.3.0")
    testImplementation("androidx.test.ext:junit:1.1.2")
    testImplementation("com.google.truth:truth:1.0.1")
    testImplementation("io.mockk:mockk:1.10.2")
    testImplementation("org.koin:koin-test:$koinVersion")
    testImplementation("org.robolectric:robolectric:4.4")
}

apply(plugin = "com.google.gms.google-services")
apply(plugin = "com.google.firebase.crashlytics")
apply(plugin = "com.google.firebase.firebase-perf")
apply(plugin = "koin")
