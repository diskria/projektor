fun RepositoryHandler.commonRepositories() {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    google()

    maven("https://libraries.minecraft.net") {
        name = "Minecraft Libraries"
    }
    maven("https://maven.fabricmc.net") {
        name = "Fabric"
    }
    exclusiveContent {
        forRepository {
            maven("https://repo.spongepowered.org/repository/maven-public") {
                name = "SpongePowered"
            }
        }
        filter {
            @Suppress("UnstableApiUsage")
            includeGroupAndSubgroups("org.spongepowered")
        }
    }
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

setupRepositories()

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
