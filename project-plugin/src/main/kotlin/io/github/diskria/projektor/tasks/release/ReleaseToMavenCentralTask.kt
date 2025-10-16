package io.github.diskria.projektor.tasks.release

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.primitives.repeat
import io.github.diskria.kotlin.utils.extensions.wrapWithDoubleQuote
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.publishing.maven.MavenCentral
import io.github.diskria.projektor.publishing.maven.common.LocalMaven.Companion.LOCAL_MAVEN_DIRECTORY_NAME
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.tasks.bundling.Zip
import org.gradle.internal.extensions.core.extra
import kotlin.io.encoding.Base64

abstract class ReleaseToMavenCentralTask : Zip() {

    init {
        val projektMetadata: ProjektMetadata by project.extra.properties

        dependsOn(MavenCentral.getConfigurePublicationTaskName())

        archiveBaseName.set(projektMetadata.repository.name)
        archiveVersion.set(projektMetadata.version)

        from(project.getBuildDirectory(LOCAL_MAVEN_DIRECTORY_NAME))
        destinationDirectory.set(project.getBuildDirectory(MavenCentral.mapToEnum().getName(`kebab-case`)))

        doLast {
            runBlocking {
                uploadBundleToSonatype()
            }
        }
    }

    suspend fun uploadBundleToSonatype() {
        val bearer = Base64.encode(
            (Secrets.sonatypeUsername + Constants.Char.COLON + Secrets.sonatypePassword).toByteArray()
        )
        val url = buildUrl("central.sonatype.com") {
            path("api", "v1", "publisher", "upload")
            parameters.append("publishingType", "AUTOMATIC")
        }

        val boundary = "----WebKitFormBoundary" + System.currentTimeMillis()
        val lineBreak = "\r\n"
        val doubleDash = Constants.Char.HYPHEN.repeat(2)

        val disposition = listOf(
            "form-data",
            "name=" + "bundle".wrapWithDoubleQuote(),
            "filename=" + archiveFileName.map { it.wrapWithDoubleQuote() }
        ).joinToString("; ")

        val bodyPrefix = buildString {
            append(doubleDash)
            append(boundary)
            append(lineBreak)
            append(HttpHeaders.ContentDisposition + ": " + disposition)
            append(lineBreak)
            append(HttpHeaders.ContentType + ": " + ContentType.Application.OctetStream)
            append(lineBreak)
            append(lineBreak)
        }.toByteArray()

        val bodySuffix = buildString {
            append(lineBreak)
            append(doubleDash)
            append(boundary)
            append(doubleDash)
            append(lineBreak)
        }.toByteArray()

        val body = bodyPrefix + archiveFile.get().asFile.readBytes() + bodySuffix
        HttpClient(CIO).use { client ->
            client.post(url) {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $bearer"
                )
                header(
                    HttpHeaders.ContentType,
                    ContentType.MultiPart.FormData.withParameter("boundary", boundary).toString()
                )
                setBody(body)
            }
        }
    }
}
