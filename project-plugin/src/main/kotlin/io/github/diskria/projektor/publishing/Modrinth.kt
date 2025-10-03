package io.github.diskria.projektor.publishing

import com.modrinth.minotaur.ModrinthExtension
import io.github.diskria.gradle.utils.extensions.kotlin.requirePlugins
import io.github.diskria.gradle.utils.extensions.kotlin.runExtension
import io.github.diskria.projektor.projekt.IProjekt
import org.gradle.api.Project

data object Modrinth : PublishingTarget {

    override val publish: Project.(IProjekt) -> Unit = configure@{ projekt ->
        requirePlugins("com.modrinth.minotaur")
        runExtension<ModrinthExtension> {
            projectId.set(projekt.slug)
        }
    }
}
