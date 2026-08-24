package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.PublishingTargetType
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.features.publishing.target.mapToModel

internal sealed class PublishingTargetShield(
    val target: PublishingTargetType,
    val metadata: ProjektMetadata,
) : LiveShield() {
    override fun getLabel(): String = target.shieldLabel
    override fun getUrl(): String = target.mapToModel().getHomepage(metadata)
    override fun getAlt(): String = getLabel()
}
