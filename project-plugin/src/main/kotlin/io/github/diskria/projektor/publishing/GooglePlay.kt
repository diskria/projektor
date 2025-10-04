package io.github.diskria.projektor.publishing

import io.github.diskria.projektor.projekt.IProjekt
import org.gradle.api.Project

data object GooglePlay : PublishingTarget {

    override val configure: Project.(IProjekt) -> Unit
        get() = TODO("Not yet implemented")
}
