package io.github.diskria.projektor.tasks.generate

import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.extensions.core.extra

abstract class UpdateGithubMetadataTask : DefaultTask() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    init {
        val projektMetadata: ProjektMetadata by project.extra.properties

        metadata.convention(projektMetadata)
    }

    @TaskAction
    fun generate() {
        val metadata = metadata.get()

        val topics = buildSet {
            add(metadata.type.getName(`kebab-case`))
            addAll(metadata.tags)
            add(metadata.publishingTarget.getName(`kebab-case`))
        }
        TODO()
    }
}
