plugins {
    alias(convention.plugins.projektor)
    alias(builder.plugins.envs.generator)
}

dependencies {
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.html)

    implementation(libs.bundles.ktor.client)

    implementation(libs.bundles.implementation.settings.plugins)
    implementation(libs.bundles.implementation.project.plugins)
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
