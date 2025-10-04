package io.github.diskria.projektor.publishing

import io.github.diskria.gradle.utils.extensions.kotlin.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.kotlin.runExtension
import io.github.diskria.projektor.projekt.IProjekt
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.maven

data object GitHubPages : PublishingTarget {

    override val configure: Project.(IProjekt) -> Unit = configure@{ _ ->
        runExtension<PublishingExtension> {
            repositories {
                maven(getBuildDirectory("repo")) {
                    name = "GitHubPages"
                }
            }
        }
    }
}
