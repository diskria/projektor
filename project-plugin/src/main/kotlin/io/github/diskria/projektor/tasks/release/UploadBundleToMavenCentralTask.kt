package io.github.diskria.projektor.tasks.release

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.Environment
import io.github.diskria.projektor.extensions.getMetadata
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.publishing.maven.MavenCentral
import io.github.diskria.projektor.publishing.maven.common.LocalMavenBasedPublishingTarget.Companion.LOCAL_MAVEN_DIRECTORY_NAME
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.ktor.util.cio.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.tasks.bundling.Zip

abstract class UploadReleaseToMavenCentralTask : Zip() {

    init {
        val metadata = project.getMetadata()
        archiveBaseName.set(metadata.repository.name)
        archiveVersion.set(metadata.version)

        from(project.getBuildDirectory(LOCAL_MAVEN_DIRECTORY_NAME))
        destinationDirectory.set(project.getBuildDirectory(MavenCentral.mapToEnum().getName(`kebab-case`)))

        if (Environment.isCI()) {
            doLast {
                runBlocking {
                    uploadBundleToSonatype()
                }
            }
        }
    }

    private suspend fun uploadBundleToSonatype() {
        val username = Environment.Secrets.sonatypeUsername
        val password = Environment.Secrets.sonatypePassword
        val bearer = (username + Constants.Char.COLON + password).toByteArray().encodeBase64()
        val url = buildUrl("central.sonatype.com") {
            path("api", "v1", "publisher", "upload")
            parameters.append("publishingType", "AUTOMATIC")
        }
        val bundleFile = archiveFile.get().asFile
        val part = PartData.FileItem({ bundleFile.readChannel() }, {}, Headers.build {
            append(
                HttpHeaders.ContentDisposition,
                ContentDisposition(ContentType.MultiPart.FormData.contentSubtype)
                    .withParameter(ContentDisposition.Parameters.Name, "bundle")
                    .withParameter(ContentDisposition.Parameters.FileName, bundleFile.name)
            )
            append(
                HttpHeaders.ContentType,
                ContentType.Application.OctetStream
            )
        })
        HttpClient(CIO).use { client ->
            client.post(url) {
                bearerAuth(bearer)
                setBody(MultiPartFormDataContent(listOf(part)))
            }
        }
    }
}
