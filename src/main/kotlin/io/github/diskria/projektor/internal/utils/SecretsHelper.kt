package io.github.diskria.projektor.internal.utils

import io.github.diskria.projektor.extensions.requireEnv
import org.gradle.api.provider.ProviderFactory

internal class SecretsHelper(private val providers: ProviderFactory) {
    val githubToken: String get() = providers.requireEnv("GITHUB_TOKEN")
    val githubPackagesToken: String get() = providers.requireEnv("GITHUB_PACKAGES_TOKEN")
    val gpgKey: String get() = providers.requireEnv("GPG_KEY")
    val gpgPassphrase: String get() = providers.requireEnv("GPG_PASSPHRASE")
    val sonatypeUsername: String get() = providers.requireEnv("SONATYPE_USERNAME")
    val sonatypePassword: String get() = providers.requireEnv("SONATYPE_PASSWORD")
    val gradlePublishKey: String get() = providers.requireEnv("GRADLE_PUBLISH_KEY")
    val gradlePublishSecret: String get() = providers.requireEnv("GRADLE_PUBLISH_SECRET")
}
