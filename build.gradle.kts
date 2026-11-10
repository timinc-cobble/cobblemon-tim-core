plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    kotlin("jvm") version ("2.4.0")

    id("dev.architectury.loom") version ("1.17-SNAPSHOT") apply false
    id("architectury-plugin") version ("3.5-SNAPSHOT") apply false
    id("com.gradleup.shadow") version ("9.6.1") apply false
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    version = "${project.property("modCobblemonVersion")}-${project.property("modMyVersion")}"
    group = project.property("maven_group") as String

    repositories {
        mavenCentral()
        maven("https://maven.impactdev.net/repository/development/")
        maven("https://maven.neoforged.net/releases")
        maven("https://thedarkcolour.github.io/KotlinForForge/")
        maven("https://maven.wispforest.io/releases/")
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }

    java {
        withSourcesJar()
    }
}

// Apply general publication configuration
subprojects {
    apply(plugin = "maven-publish")

    // IS_SNAPSHOT env var trickles from publish-maven gh actions workflow.
    // 1 - when triggered manually
    // 0 - when triggered by published release
    val isSnapshot = System.getenv("IS_SNAPSHOT").toIntOrNull() ?: 0

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                if (isSnapshot != 0) {
                    version = "${project.version as String}-SNAPSHOT"
                }

                artifact(tasks.getByName("remapJar"))
                artifact(tasks.getByName("remapSourcesJar"))
            }
        }

        repositories {
            maven {
                // silly derive the appropriate publication repo
                val repo = when (isSnapshot) {
                    1 -> "snapshots"
                    else -> "releases"
                }

                // Set these variables in gh action secrets
                if (System.getenv("CCM_USER") == null || System.getenv("CCM_PASS") == null) {
                    throw RuntimeException("Did you forget your publication credentials?")
                }

                url = uri("https://maven.cobbled-creators.org/$repo")
                credentials {
                    username = System.getenv("CCM_USER").toString()
                    password = System.getenv("CCM_PASS").toString()
                }
            }
        }
    }

    tasks {
        withType<GenerateModuleMetadata> {
            enabled = false
        }
    }
}
