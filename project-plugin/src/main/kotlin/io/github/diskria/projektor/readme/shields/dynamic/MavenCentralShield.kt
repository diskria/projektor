package io.github.diskria.projektor.readme.shields.dynamic

import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadataExtra
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.publishing.maven.MavenCentral

class MavenCentralShield(metadata: ProjektMetadataExtra) : DynamicShield(metadata) {

    override fun getPathParts(): List<String> {
        val repository = metadata.repository
        return listOf(MavenCentral.mapToEnum().getName(`kebab-case`), "v", repository.owner.namespace, repository.name)
    }
}
