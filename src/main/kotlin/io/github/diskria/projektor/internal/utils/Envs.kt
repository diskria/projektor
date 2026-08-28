package io.github.diskria.projektor.internal.utils

import org.gradle.api.provider.ProviderFactory

internal class Envs(private val providers: ProviderFactory) {

    val isCI: Boolean get() = getEnvOrNull("CI")?.toBoolean() == true
    val githubOwner: String get() = getEnv("GITHUB_OWNER")
    val githubRepo: String get() = getEnv("GITHUB_REPO")
    val githubToken: String get() = getEnv("GITHUB_TOKEN")
    val githubPackagesToken: String get() = getEnv("GITHUB_PACKAGES_TOKEN")
    val gpgKey: String get() = getEnv("GPG_KEY")
    val gpgPassphrase: String get() = getEnv("GPG_PASSPHRASE")
    val sonatypeUsername: String get() = getEnv("SONATYPE_USERNAME")
    val sonatypePassword: String get() = getEnv("SONATYPE_PASSWORD")
    val gradlePublishKey: String get() = getEnv("GRADLE_PUBLISH_KEY")
    val gradlePublishSecret: String get() = getEnv("GRADLE_PUBLISH_SECRET")

    private fun getEnvOrNull(name: String): String? =
        providers.environmentVariable(name).orNull

    private fun getEnv(name: String): String =
        getEnvOrNull(name) ?: Errors.internal.error("Environment variable '$name' is required but not set!")
}
