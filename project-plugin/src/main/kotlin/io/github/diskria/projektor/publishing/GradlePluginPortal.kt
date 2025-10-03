package io.github.diskria.projektor.publishing

import io.github.diskria.projektor.projekt.IProjekt
import org.gradle.api.Project

data object GradlePluginPortal : PublishingTarget {

    override val publish: Project.(IProjekt) -> Unit = configure@{ projekt ->

    }
}
