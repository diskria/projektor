package io.github.diskria.projektor.publishing

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.maven

data object LocalMaven : PublishingTarget {

    override val configure: Project.(IProjekt) -> Unit = configure@{ _ ->
        runExtension<PublishingExtension> {
            repositories {
                maven(getBuildDirectory("localMaven")) {
                    name = "LocalMaven"
                }
            }
        }
    }
}
