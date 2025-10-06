package io.github.diskria.projektor.publishing

import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project

sealed interface PublishingTarget {
    val configure: Project.(IProjekt) -> Unit
}
