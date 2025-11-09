package io.github.diskria.projektor.configurators

import io.github.diskria.projektor.configurations.AndroidApplicationConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.extensions.toProjekt
import io.github.diskria.projektor.projekt.AndroidApplication
import org.gradle.api.Project

open class AndroidApplicationConfigurator(
    val config: AndroidApplicationConfiguration
) : ProjectConfigurator<AndroidApplication>() {

    override fun buildProjekt(project: Project): AndroidApplication =
        project.toProjekt().toAndroidApplication(config)

    override fun configureProject(project: Project, projekt: AndroidApplication) {

    }
}
