package io.github.diskria.projektor.publishing

import io.github.diskria.gradle.utils.extensions.kotlin.getBuildDirectory
import io.github.diskria.projektor.extensions.kotlin.publishing
import io.github.diskria.projektor.projekt.IProjekt
import org.gradle.api.Project
import org.gradle.kotlin.dsl.maven

data object GitHubPages : PublishingTarget {

    override val configurePublishing: Project.(IProjekt) -> Unit = configure@{ _ ->
        publishing {
            repositories {
                maven(getBuildDirectory("repo")) {
                    name = "GitHubPages"
                }
            }
        }
    }
}
