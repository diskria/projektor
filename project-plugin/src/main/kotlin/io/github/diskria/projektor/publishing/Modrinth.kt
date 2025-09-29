package io.github.diskria.projektor.publishing

import io.github.diskria.gradle.utils.extensions.kotlin.requirePlugins
import io.github.diskria.projektor.extensions.kotlin.modrinth
import io.github.diskria.projektor.projekt.IProjekt
import org.gradle.api.Project

data object Modrinth : PublishingTarget {

    override val configurePublishing: Project.(IProjekt) -> Unit = configure@{ projekt ->
        requirePlugins("com.modrinth.minotaur")
        modrinth {
            projectId.set(projekt.slug)
        }
    }
}
