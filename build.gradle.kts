import org.jetbrains.kotlin.gradle.utils.extendsFrom
import java.util.Properties

plugins {
    kotlin("jvm") version "2.0.0"
    idea
    // https://projects.neoforged.net/neoforged/moddevgradle
    id("net.neoforged.moddev") version "0.1.117"
}

tasks.named<Wrapper>("wrapper") {
    // Define wrapper values here so as to not have to always do so when updating gradlew.properties.
    // Switching this to Wrapper.DistributionType.ALL will download the full gradle sources that comes with
    // documentation attached on cursor hover of gradle classes and methods. However, this comes with increased
    // file size for Gradle. If you do switch this to ALL, run the Gradle wrapper task twice afterwards.
    // (Verify by checking gradle/wrapper/gradle-wrapper.properties to see if distributionUrl now points to `-all`)
    distributionType = Wrapper.DistributionType.BIN
}

val mod_id: String by project
version = project.property("version") as String
group = project.property("group") as String

repositories {
    mavenCentral()
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

base {
    archivesName = mod_id
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

val neoVersion: String by project

val nonModImpl: Configuration by configurations.creating
configurations.implementation.extendsFrom(configurations.named(nonModImpl.name))

neoForge {
    version = neoVersion

    runs {
        configureEach {
            dependencies {
                // Adds non-mod dependencies to the runtime classpath for Minecraft runs
                additionalRuntimeClasspathConfiguration.extendsFrom(nonModImpl)
            }


            // Recommended logging data for a userdev environment
            // The markers can be added/remove as needed separated by commas.
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            systemProperty("forge.logging.markers", "REGISTRIES")
        }
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }
        create("data") {
            data()
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }
        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }
    }

    mods {
        create(mod_id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

val dl4jVersion: String by project

val kotlinForgeVersion: String by project

dependencies {
    // Neural Network stuff
    nonModImpl("org.deeplearning4j:deeplearning4j-core:$dl4jVersion")
    nonModImpl("org.nd4j:nd4j-native-platform:$dl4jVersion")

    // Kotlin stuff
    implementation("thedarkcolour:kotlinforforge-neoforge:$kotlinForgeVersion")
}

// This block of code expands all declared replace properties in the specified resource targets.
// A missing property will result in an error. Properties are expanded using ${} Groovy notation.
// When "copyIdeResources" is enabled, this will also run before the game launches in IDE environments.
// See https://docs.gradle.org/current/dsl/org.gradle.language.jvm.tasks.ProcessResources.html
tasks.withType<ProcessResources> {
    @Suppress("UNCHECKED_CAST")
    val loadedProperties = Properties().apply {
        load(project.rootProject.file("gradle.properties").inputStream())
    }.toMutableMap() as MutableMap<String, Any>

    inputs.properties(loadedProperties)

    filesMatching(listOf("META-INF/neoforge.mods.toml", "*.mixins.json")) {
        expand(loadedProperties)
    }
}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}