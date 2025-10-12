package io.github.diskria.projektor.markdown.shields

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.projekt.common.IProjekt
import io.ktor.http.*

class MavenCentralShield(projekt: IProjekt) : DynamicShield(
    pathParts = listOf("maven-central", "v", projekt.namespace, projekt.repo),
    label = "Maven Central",
    url = buildUrl("central.sonatype.com", URLProtocol.HTTPS) {
        path("artifact", projekt.namespace, projekt.repo)
    }
)
