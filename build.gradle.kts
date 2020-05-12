// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlin_version: String by extra
    val gradle_android_version: String by extra
    repositories {
        maven("https://dl.bintray.com/kotlin/kotlin-dev")
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.0-alpha08")
//        classpath("com.github.jengelman.gradle.plugins:shadow:5.0.0")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()

        google()
        jcenter()
    }
}

task<Delete>("clean") {
    delete { rootProject.buildDir }
}