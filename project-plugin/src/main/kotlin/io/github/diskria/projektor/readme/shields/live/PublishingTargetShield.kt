package io.github.diskria.projektor.readme.shields.live

import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.publishing.PublishingTargetType
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.ktor.http.*

sealed class PublishingTargetShield(val target: PublishingTargetType, val metadata: ProjektMetadata) : LiveShield() {

    override fun getLabel(): String =
        target.getName(`Title Case`)

    override fun getUrl(): Url =
        target.mapToModel().getHomepage(metadata)

    override fun getAlt(): String =
        getLabel()
}
