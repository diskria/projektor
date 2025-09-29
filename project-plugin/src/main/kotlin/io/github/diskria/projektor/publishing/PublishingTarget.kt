package io.github.diskria.projektor.publishing

import io.github.diskria.projektor.projekt.IProjekt
import org.gradle.api.Project

sealed interface PublishingTarget {
    val configurePublishing: Project.(IProjekt) -> Unit
}
