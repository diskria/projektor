package io.github.diskria.projektor.tasks.distribute

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.publishing.maven.MavenCentral
import io.github.diskria.projektor.publishing.maven.common.LocalMavenBasedPublishingTarget
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.ktor.util.cio.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.tasks.bundling.Zip

abstract class UploadBundleToMavenCentralTask : Zip() {

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        val metadata = project.getProjektMetadata()
        archiveBaseName.set(metadata.repo.name)
        archiveVersion.set(metadata.version)

        from(project.getBuildDirectory(LocalMavenBasedPublishingTarget.LOCAL_MAVEN_DIRECTORY_NAME))
        destinationDirectory.set(project.getBuildDirectory(MavenCentral.mapToEnum().getName(`kebab-case`)))

        doLast {
            println("[UploadBundleToMavenCentralTask] start")
            if (!EnvironmentHelper.isCI()) {
                println("[UploadBundleToMavenCentralTask] not running on CI, stop")
                return@doLast
            }
            runBlocking {
                uploadBundle()
            }
            println("[UploadBundleToMavenCentralTask] end")
        }
    }

    private suspend fun uploadBundle() {
        val username = Secrets.sonatypeUsername
        val password = Secrets.sonatypePassword
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
                    .withParameter(ContentDisposition.Parameters.Name, FORM_NAME)
                    .withParameter(ContentDisposition.Parameters.FileName, bundleFile.name)
            )
            append(
                HttpHeaders.ContentType,
                ContentType.Application.OctetStream
            )
        })
        HttpClient(CIO).use { client ->
            val response = client.post(url) {
                bearerAuth(bearer)
                setBody(MultiPartFormDataContent(listOf(part)))
            }
            println("[UploadBundleToMavenCentralTask] response = " + response.bodyAsText())
        }
    }

    companion object {
        private const val FORM_NAME: String = "bundle"
    }
}
