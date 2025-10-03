package io.github.diskria.projektor.publishing

import com.modrinth.minotaur.ModrinthExtension
import io.github.diskria.gradle.utils.extensions.kotlin.getExtensionOrThrow
import io.github.diskria.gradle.utils.extensions.kotlin.requirePlugins
import io.github.diskria.projektor.projekt.IProjekt
import org.gradle.api.Project

data object Modrinth : PublishingTarget {

    override val configurePublishing: Project.(IProjekt) -> Unit = configure@{ projekt ->
        requirePlugins("com.modrinth.minotaur")
        getExtensionOrThrow<ModrinthExtension>().apply {
            projectId.set(projekt.slug)
        }
    }
}
