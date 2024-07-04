plugins {
    kotlin("jvm") version "2.0.0"
    idea
    application
}

application {
    mainClass = "dev.erdragh.kai.network.TestKt"
    mainModule = "KAI.main"
}

repositories {
    mavenCentral()
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

val dl4jVersion: String by project

dependencies {
    // Neural Network stuff
    implementation("org.deeplearning4j:deeplearning4j-core:$dl4jVersion") {
        exclude(module = "slf4j.api")
    }
    implementation("org.nd4j:nd4j-native-platform:$dl4jVersion") {
        exclude(module = "slf4j.api")
    }
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.slf4j:slf4j-simple:2.0.13")

}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}