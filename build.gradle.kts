import org.jetbrains.kotlin.gradle.utils.extendsFrom
import java.util.Properties

plugins {
    kotlin("jvm") version "2.0.0"
    idea
    // Apply the plugin. You can find the latest version at https://github.com/neoforged/ModDevGradle/packages/2159800.
    id("net.neoforged.moddev") version "0.1.74"
}

group = project.property("group") as String
version = project.property("version") as String

repositories {
    mavenCentral()
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

val neoVersion: String by project

val nonModImpl by configurations.creating {
    isTransitive = true
}
configurations.implementation.extendsFrom(configurations.named(nonModImpl.name))

neoForge {
    // We currently only support NeoForge versions later than 21.0.x
    version = neoVersion

    runs {
        configureEach {
            dependencies {
                additionalRuntimeClasspathConfiguration.extendsFrom(nonModImpl)
            }
        }
        create("client") {
            client()
        }
        create("data") {
            data()
        }
        create("server") {
            server()
        }
    }

    mods {
        create("kai") {
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

tasks {
    processResources {
        @Suppress("UNCHECKED_CAST")
        val loadedProperties = Properties().apply {
            load(project.rootProject.file("gradle.properties").inputStream())
        }.toMutableMap() as MutableMap<String, Any>

        inputs.properties(loadedProperties)

        filesMatching(listOf("META-INF/neoforge.mods.toml", "*.mixins.json")) {
            expand(loadedProperties)
        }
    }
}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}