package io.github.diskria.projektor.configurators

import io.github.diskria.projektor.configurations.AndroidApplicationConfiguration
import io.github.diskria.projektor.projekt.AndroidApplication
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project

open class AndroidApplicationConfigurator(
    val config: AndroidApplicationConfiguration
) : Configurator<AndroidApplication>() {

    override fun configure(project: Project, projekt: IProjekt): AndroidApplication {
        val androidApplication = AndroidApplication(projekt, config)
        applyCommonConfiguration(project, androidApplication)
        return androidApplication
    }
}
