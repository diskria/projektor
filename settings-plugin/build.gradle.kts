import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.projektor.common.projekt.ProjektModules

plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor)
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(project(ProjektModules.COMMON_PATH))

    implementation(libs.bundles.diskria.utils)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.implementation.settings.plugins)
}

tasks {
    shadowJar {
        archiveClassifier = Constants.Char.EMPTY
        configurations = emptyList()

        val jarTask = project(ProjektModules.COMMON_PATH).tasks.jar
        dependsOn(jarTask)
        from(zipTree(jarTask.flatMap { it.archiveFile }))
    }
}

projekt {
    gradlePlugin {
        isSettingsPlugin = true
    }
}
