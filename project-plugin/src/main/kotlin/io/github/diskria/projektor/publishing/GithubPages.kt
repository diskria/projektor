package io.github.diskria.projektor.publishing

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendSuffix
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.readme.shields.GithubPagesMavenShield
import io.github.diskria.projektor.readme.shields.ReadmeShield
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.withType

data object GithubPages : PublishingTarget {

    override fun configure(projekt: IProjekt, project: Project) = with(project) {
        runExtension<PublishingExtension> {
            publications.withType<MavenPublication> {
                artifactId = projekt.repo.modifyIf(project != rootProject) {
                    it.appendSuffix(Constants.Char.HYPHEN + project.name)
                }
            }
            repositories {
                maven(getBuildDirectory("localMaven")) {
                    name = getTypeName()
                }
            }
        }
    }

    override fun getReadmeShield(projekt: IProjekt): ReadmeShield =
        GithubPagesMavenShield(projekt)
}
