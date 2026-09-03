import builder.metadata_generator.extensions.getByType
import builder.metadata_generator.tasks.GenerateBuildConfigTask
import io.github.diskria.projektor.core.model.GradlePlugin

plugins {
    alias(convention.plugins.projektor)
    alias(builder.plugins.metadata.generator)
}

dependencies {
    implementation(libs.bundles.embedded.plugins)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.html)
    implementation(libs.bundles.ktor.client)
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

tasks.getByType<GenerateBuildConfigTask>().apply {
    val gradlePlugin = projekt.map { it as GradlePlugin.Distributable }
    pluginId.set(gradlePlugin.map { it.id })
    pluginVersion.set(gradlePlugin.map { it.version })
}
