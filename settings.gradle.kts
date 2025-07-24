pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven ("https://repo1.maven.org/maven2")
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "cells-sdk-kmp"

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("awssdk") {
            from("aws.sdk.kotlin:version-catalog:1.4.125")
        }
    }
}
