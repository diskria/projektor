package io.github.diskria.projektor.api

import io.github.diskria.projektor.core.configurators.GradlePluginConfigurator
import io.github.diskria.projektor.core.configurators.KotlinLibraryConfigurator
import io.github.diskria.projektor.core.configurators.ProjektConfigurator
import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class DistributableProjektExtension @Inject internal constructor(
    private val objects: ObjectFactory,
) : ProjektorScope {

    internal val distributionTargets = objects.listProperty<DistributionTargetType>()
    internal val projekt = objects.property<Projekt.Distributable>()

    private var configurator: ProjektConfigurator<*, *, *>? = null

    fun gradlePlugin(configure: GradlePluginConfiguration.() -> Unit = {}) {
        setConfigurator(GradlePluginConfigurator(objects.newInstance<GradlePluginConfiguration>().apply(configure)))
    }

    fun kotlinLibrary(configure: KotlinLibraryConfiguration.() -> Unit = {}) {
        setConfigurator(KotlinLibraryConfigurator(objects.newInstance<KotlinLibraryConfiguration>().apply(configure)))
    }

    fun distribute(configure: DistributionDsl.() -> Unit) {
        DistributionDsl(distributionTargets).configure()
    }

    fun <T : Any> map(transform: (Projekt.Distributable) -> T): Provider<T> =
        projekt.map(transform)

    internal fun ensureConfigured(
        project: Project,
        projektMetadata: ProjektMetadata.Distributable,
    ): Projekt.Distributable {
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
