package io.github.diskria.projektor.publishing.maven.common

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.projekt.common.Projekt
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.maven

abstract class LocalMavenBasedPublishingTarget : MavenPublishingTarget() {

    fun getLocalMavenDirectory(project: Project): Provider<Directory> =
        project.getBuildDirectory("maven/" + this::class.className().setCase(PascalCase, `kebab-case`))

    override fun configureMaven(
        repositories: RepositoryHandler,
        projekt: Projekt,
        project: Project,
    ): MavenArtifactRepository = with(repositories) {
        maven(getLocalMavenDirectory(project)) {
            name = getRepositoryName()
        }
    }
}
