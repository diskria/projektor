package io.github.diskria.projektor.publishing

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.withType

data object LocalMaven : PublishingTarget {

    override fun configure(projekt: IProjekt, project: Project) = with(project) {
        runExtension<PublishingExtension> {
            repositories {
                maven(getBuildDirectory("localMaven")) {
                    name = "LocalMaven"
                }
            }
        }
    }
}
