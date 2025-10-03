package io.github.diskria.projektor.publishing

import io.github.diskria.projektor.projekt.IProjekt
import org.gradle.api.Project

sealed interface PublishingTarget {
    val publish: Project.(IProjekt) -> Unit
}
