plugins {
    kotlin("jvm") version "2.0.0"
    application
}

group = project.property("group") as String
version = project.property("version") as String

application.mainClass = "dev.erdragh.kai.MainKt"

repositories {
    mavenCentral()
}

val dl4jVersion: String by project
val slf4jVersion: String by project

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.deeplearning4j:deeplearning4j-core:$dl4jVersion")
    implementation("org.nd4j:nd4j-native-platform:$dl4jVersion")

    runtimeOnly("org.slf4j:slf4j-simple:$slf4jVersion")
}

tasks.test {
    useJUnitPlatform()
}