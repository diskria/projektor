package io.github.diskria.projektor.api

import io.github.diskria.projektor.core.configurators.GradlePluginConfigurator
import io.github.diskria.projektor.core.configurators.KotlinLibraryConfigurator
import io.github.diskria.projektor.core.configurators.ProjectConfigurator
import io.github.diskria.projektor.core.model.PublishingTargetType
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.check
import io.github.diskria.projektor.internal.utils.checkNotNull
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class ProjektExtension @Inject internal constructor(
    private val metadata: ProjektMetadata,
    private val objects: ObjectFactory,
) : ProjektorScope {

    internal val publishingTargets = objects.listProperty<PublishingTargetType>()

    private var configurator: ProjectConfigurator<*>? = null

    fun gradlePlugin(configure: GradlePluginDsl.() -> Unit = {}): GradlePluginRef {
        setConfigurator(GradlePluginConfigurator(objects.newInstance<GradlePluginDsl>().apply(configure)))
        return GradlePluginRef(id = metadata.packageName)
    }

    fun kotlinLibrary(configure: KotlinLibraryDsl.() -> Unit = {}) {
        setConfigurator(KotlinLibraryConfigurator(objects.newInstance<KotlinLibraryDsl>().apply(configure)))
    }

    fun publishing(configure: PublishingDsl.() -> Unit) {
        PublishingDsl(publishingTargets).configure()
    }

    internal fun ensureConfigured(project: Project) {
        Errors.frontend.checkNotNull(configurator) { "Projekt not configured" }.configure(project)
    }

    private fun setConfigurator(configurator: ProjectConfigurator<*>) {
        Errors.frontend.check(this.configurator == null) { "Projekt already configured" }
        this.configurator = configurator
    }
}
