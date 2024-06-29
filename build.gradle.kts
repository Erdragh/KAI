import org.jetbrains.kotlin.gradle.utils.extendsFrom
import java.util.Properties

plugins {
    kotlin("jvm") version "2.0.0"
    idea
    // Apply the plugin. You can find the latest version at https://github.com/neoforged/ModDevGradle/packages/2159800.
    id("net.neoforged.moddev") version "0.1.116"
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

val moduleFixesForDL4J = listOf("slf4j", "commons", "guava", "jackson", "lombok", "oshi.core", "protobuf", "fastutil", "resources", "gson", "commons_codec", "fx_graphics")

sourceSets {
    moduleFixesForDL4J.forEach {
        create("fix_$it") {
            java {}
        }
    }
}

val neoVersion: String by project

val nonModImpl: Configuration by configurations.creating
configurations.implementation.extendsFrom(configurations.named(nonModImpl.name))

neoForge {
    moduleFixesForDL4J.forEach {
        addModdingDependenciesTo(sourceSets.named("fix_$it").get())
    }

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
    nonModImpl("org.deeplearning4j:deeplearning4j-core:$dl4jVersion") {
        exclude(module = "org.slf4j")
        exclude(module = "slf4j-api")
        exclude(group = "slf4j")
        exclude(group = "org.slf4j")
        exclude(module = "commons.io")
        exclude(module = "guava")
    }
    nonModImpl("org.nd4j:nd4j-native-platform:$dl4jVersion") {
        exclude(module = "org.slf4j")
        exclude(module = "slf4j-api")
        exclude(group = "slf4j")
        exclude(group = "org.slf4j")
        exclude(module = "commons.io")
        exclude(module = "guava")
    }
    moduleFixesForDL4J.forEach {
        nonModImpl(sourceSets.named("fix_$it").get().output)
    }

    // Kotlin stuff
    implementation("thedarkcolour:kotlinforforge-neoforge:$kotlinForgeVersion")
}

tasks {
    withType<ProcessResources>().configureEach {
        @Suppress("UNCHECKED_CAST")
        val loadedProperties = Properties().apply {
            load(project.rootProject.file("gradle.properties").inputStream())
        }.toMutableMap() as MutableMap<String, Any>

        inputs.properties(loadedProperties)

        filesMatching(listOf("**/*.mods.toml", "**/*.mixins.json")) {
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