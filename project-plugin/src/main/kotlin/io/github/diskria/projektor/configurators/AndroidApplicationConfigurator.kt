package io.github.diskria.projektor.configurators

import io.github.diskria.projektor.configurations.AndroidApplicationConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.projekt.AndroidApplication
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project

open class AndroidApplicationConfigurator(
    val config: AndroidApplicationConfiguration = AndroidApplicationConfiguration()
) : ProjectConfigurator<AndroidApplication>() {

    override fun configureProject(project: Project, projekt: IProjekt): AndroidApplication =
        AndroidApplication(projekt, config)
}
