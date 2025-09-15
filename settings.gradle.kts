//import io.github.diskria.projektor.settings.extensions.configureAndroidApp
//import io.github.diskria.projektor.settings.extensions.configureMinecraftMod
//import io.github.diskria.projektor.settings.extensions.configureProject

//pluginManagement {
//    repositories {
//        mavenCentral()
//        maven("https://diskria.github.io/projektor")
//    }
//}
//
//plugins {
//    id("io.github.diskria.projektor.settings") version "1.+"
//}
//
//configureProject()
//configureMinecraftMod()
//configureAndroidApp()

//
rootProject.name = providers.gradleProperty("project.name").get()

fun RepositoryHandler.commonRepositories() {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven("https://diskria.github.io/projektor") {
        name = "Projektor"
    }
    maven("https://libraries.minecraft.net") {
        name = "MinecraftLibraries"
    }
    maven("https://maven.fabricmc.net") {
        name = "Fabric"
    }
    setupExclusiveContent(
        maven("https://repo.spongepowered.org/repository/maven-public") {
            name = "SpongePowered"
        },
        "org.spongepowered",
        isSubgroupsAllowed = true
    )
    setupExclusiveContent(
        maven("https://api.modrinth.com/maven") {
            name = "Modrinth"
        },
        "maven.modrinth"
    )
}

fun setupRepositories() {
    dependencyResolutionManagement {
        @Suppress("UnstableApiUsage")
        repositories {
            commonRepositories()
        }
    }

    pluginManagement {
        repositories {
            commonRepositories()
        }
    }
}

private fun RepositoryHandler.setupExclusiveContent(
    maven: MavenArtifactRepository,
    groupFilter: String,
    isSubgroupsAllowed: Boolean = false,
) {
    exclusiveContent {
        forRepository {
            maven
        }
        filter {
            if (isSubgroupsAllowed) {
                @Suppress("UnstableApiUsage")
                includeGroupAndSubgroups(groupFilter)
            } else {
                includeGroup(groupFilter)
            }
        }
    }
}

setupRepositories()
//

include(":project-plugin", ":settings-plugin")
