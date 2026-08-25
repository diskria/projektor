package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.PublishingTargetType
import io.github.diskria.projektor.features.publishing.target.mapToModel

internal sealed class PublishingTargetShield(
    val target: PublishingTargetType,
    val projekt: Projekt,
) : LiveShield() {
    override fun getLabel(): String = target.shieldLabel
    override fun getUrl(): String? = target.mapToModel().getHomepage(projekt)
    override fun getAlt(): String = getLabel()
}
