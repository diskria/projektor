package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.features.distribution.target.mapToModel

internal sealed class DistributionTargetShield(
    val target: DistributionTargetType,
    val projekt: Projekt,
) : LiveShield() {
    override fun getLabel(): String = target.displayName
    override fun getUrl(): String? = target.mapToModel().getHomepage(projekt)
    override fun getAlt(): String = getLabel()
}
