package io.github.diskria.projektor.internal.utils

import org.gradle.api.provider.ProviderFactory

internal class Envs(private val providers: ProviderFactory) {

    val isCI: Boolean get() = envOrNull("CI")?.toBoolean() == true
    val githubOwner: String get() = env("GITHUB_OWNER")
    val githubRepo: String get() = env("GITHUB_REPO")
    val githubToken: String get() = env("GITHUB_TOKEN")
    val githubPackagesToken: String get() = env("GITHUB_PACKAGES_TOKEN")
    val gpgKey: String get() = env("GPG_KEY")
    val gpgPassphrase: String get() = env("GPG_PASSPHRASE")
    val sonatypeUsername: String get() = env("SONATYPE_USERNAME")
    val sonatypePassword: String get() = env("SONATYPE_PASSWORD")
    val gradlePublishKey: String get() = env("GRADLE_PUBLISH_KEY")
    val gradlePublishSecret: String get() = env("GRADLE_PUBLISH_SECRET")

    internal fun envOrNull(name: String): String? =
        providers.environmentVariable(name).orNull

    internal fun env(name: String): String =
        envOrNull(name) ?: Errors.internal.error("Environment variable '$name' is required but not set!")
}
