package io.github.diskria.projektor.readme.shields.dynamic

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.projekt.metadata.github.GithubRepository
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.publishing.maven.MavenCentral
import io.ktor.http.*

class MavenCentralShield(repository: GithubRepository) : DynamicShield(
    pathParts = listOf(
        MavenCentral.mapToEnum().getName(`kebab-case`),
        "v",
        repository.owner.namespace,
        repository.name
    ),
    publishingTarget = MavenCentral,
    url = buildUrl("central.sonatype.com") {
        path("artifact", repository.owner.namespace, repository.name)
    }
)
