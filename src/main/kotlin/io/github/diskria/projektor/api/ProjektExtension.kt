package io.github.diskria.projektor.api

import io.github.diskria.projektor.core.configurators.GradlePluginConfigurator
import io.github.diskria.projektor.core.configurators.KotlinLibraryConfigurator
import io.github.diskria.projektor.core.configurators.ProjektConfigurator
import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class ProjektExtension @Inject internal constructor(private val objects: ObjectFactory) : ProjektorScope {

    internal val distributionTargets = objects.listProperty<DistributionTargetType>()
    internal val configuredProjekt: Property<Projekt> = objects.property()

    private var configurator: ProjektConfigurator<*>? = null

    fun gradlePlugin(configure: GradlePluginDsl.() -> Unit = {}) {
        setConfigurator(GradlePluginConfigurator(objects.newInstance<GradlePluginDsl>().apply(configure)))
    }

    fun kotlinLibrary(configure: KotlinLibraryDsl.() -> Unit = {}) {
        setConfigurator(KotlinLibraryConfigurator(objects.newInstance<KotlinLibraryDsl>().apply(configure)))
    }

    fun distribute(configure: DistributionDsl.() -> Unit) {
        DistributionDsl(distributionTargets).configure()
    }

    internal fun ensureConfigured(project: Project, projektMetadata: ProjektMetadata): Projekt {
        val projekt = checkNotNull(configurator) {
            "Projekt configuration is missing in '${project.path}' build script! Please call 'projekt { ... }'."
        }.configure(project, projektMetadata)
        configuredProjekt.set(projekt)
        return projekt
    }

    private fun setConfigurator(configurator: ProjektConfigurator<*>) {
        check(this.configurator == null) { "Projekt already configured" }
        this.configurator = configurator
    }
}
