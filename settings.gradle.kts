rootProject.name = "KAI"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        // Add the usual NeoForged maven repository.
        maven(url = "https://maven.neoforged.net/releases")
    }
}

plugins {
    // This plugin allows Gradle to automatically download arbitrary versions of Java for you
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}