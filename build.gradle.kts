buildscript {
    repositories {
        jcenter()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath ("com.google.gms:google-services:4.3.5")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.5.2")
        classpath("com.google.firebase:perf-plugin:1.3.5")
        classpath("org.koin:koin-gradle-plugin:2.2.2")
        classpath(kotlin("gradle-plugin", version = "1.4.31"))
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
}

val clean by tasks.registering(Delete::class) {
    delete(rootProject.buildDir)
}
