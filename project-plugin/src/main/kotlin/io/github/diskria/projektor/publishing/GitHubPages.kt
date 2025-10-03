package io.github.diskria.projektor.publishing

import io.github.diskria.gradle.utils.extensions.kotlin.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.kotlin.getExtensionOrThrow
import io.github.diskria.projektor.projekt.IProjekt
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.maven

data object GitHubPages : PublishingTarget {

    override val configurePublishing: Project.(IProjekt) -> Unit = configure@{ _ ->
        getExtensionOrThrow<PublishingExtension>().apply {
            repositories {
                maven(getBuildDirectory("repo")) {
                    name = "GitHubPages"
                }
            }
        }
    }
}
