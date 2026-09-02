import builder.metadata_generator.tasks.GenerateBuildConfigTask
import io.github.diskria.projektor.core.model.GradlePlugin

plugins {
    alias(convention.plugins.projektor)
    alias(builder.plugins.metadata.generator)
}

dependencies {
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.html)

    implementation(libs.bundles.ktor.client)

    implementation(libs.bundles.implementation.settings.plugins)
    implementation(libs.bundles.implementation.project.plugins)

    implementation(libs.snake.yaml)
}

projekt {
    gradlePlugin {
        supportsConfigurationCache = true
    }
    distribute {
        mavenLocal()
        gradlePluginPortal()
    }
}

tasks.withType<GenerateBuildConfigTask>().configureEach {
    val gradlePlugin = projekt.map { it as GradlePlugin.Distributable }
    pluginId.set(gradlePlugin.map { it.id })
    pluginVersion.set(gradlePlugin.map { it.version })
}
