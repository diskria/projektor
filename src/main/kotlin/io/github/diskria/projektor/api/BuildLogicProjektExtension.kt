package io.github.diskria.projektor.api

import io.github.diskria.projektor.core.configurators.GradlePluginConfigurator
import io.github.diskria.projektor.core.configurators.KotlinLibraryConfigurator
import io.github.diskria.projektor.core.configurators.ProjektConfigurator
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class BuildLogicProjektExtension @Inject internal constructor(
    private val objects: ObjectFactory,
) : ProjektorScope {

    internal val projekt = objects.property<Projekt.BuildLogic>()

    private var configurator: ProjektConfigurator<*, *, *>? = null

    fun gradlePlugin(configure: GradlePluginConfiguration.() -> Unit = {}) {
        setConfigurator(GradlePluginConfigurator(objects.newInstance<GradlePluginConfiguration>().apply(configure)))
    }

    fun kotlinLibrary(configure: KotlinLibraryConfiguration.() -> Unit = {}) {
        setConfigurator(KotlinLibraryConfigurator(objects.newInstance<KotlinLibraryConfiguration>().apply(configure)))
    }

    fun <T : Any> map(transform: (Projekt.BuildLogic) -> T): Provider<T> =
        projekt.map(transform)

    internal fun ensureConfigured(project: Project, projektMetadata: ProjektMetadata.BuildLogic): Projekt.BuildLogic {
        val configurator = checkNotNull(configurator) {
            "Projekt configuration is missing in '${project.path}' build script! Please call 'projekt { ... }'."
        }
        return configurator.configure(project, projektMetadata).also { projekt.set(it) }
    }

    private fun setConfigurator(configurator: ProjektConfigurator<*, *, *>) {
        check(this.configurator == null) { "Projekt already configured" }
        this.configurator = configurator
    }
}
