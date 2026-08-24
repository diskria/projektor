package io.github.diskria.projektor.features.publishing.tasks

import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.extensions.isCI
import io.github.diskria.projektor.extensions.projektMetadata
import io.github.diskria.projektor.features.publishing.target.MavenCentral
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.ProjektorHttpClient
import io.github.diskria.projektor.internal.utils.SecretsHelper
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.cio.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.tasks.bundling.Zip
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject
import kotlin.io.encoding.Base64

@DisableCachingByDefault(because = "Uploads a ZIP bundle to Maven Central via API")
internal abstract class UploadBundleToMavenCentralTask @Inject constructor(private val secrets: SecretsHelper) : Zip() {

    init {
        applyProjektorGroup()

        val metadata = project.projektMetadata
        archiveBaseName.set(metadata.repo.name)
        archiveVersion.set(metadata.version)

        from(MavenCentral.getLocalMavenDirectory(project))
        destinationDirectory.set(project.layout.buildDirectory.dir("maven-central"))

        if (project.providers.isCI) {
            doLast {
                runBlocking {
                    uploadBundle()
                }
            }
        }
    }

    private suspend fun uploadBundle() {
        val bundleFile = archiveFile.get().asFile

        val part = PartData.FileItem(
            provider = { bundleFile.readChannel() },
            dispose = {},
            partHeaders = Headers.build {
                append(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition(ContentType.MultiPart.FormData.contentSubtype).apply {
                        withParameter(ContentDisposition.Parameters.Name, "bundle")
                        withParameter(ContentDisposition.Parameters.FileName, bundleFile.name)
                    }
                )
                append(
                    HttpHeaders.ContentType,
                    ContentType.Application.OctetStream
                )
            }
        )
        ProjektorHttpClient.client.use { client ->
            val url = "https://central.sonatype.com/api/v1/publisher/upload?publishingType=AUTOMATIC"
            val token = secrets.sonatypeUsername + ":" + secrets.sonatypePassword
            val response = client.post(url) {
                bearerAuth(Base64.encode(token.toByteArray()))
                setBody(MultiPartFormDataContent(listOf(part)))

                onUpload { bytesSent, totalBytes ->
                    if (totalBytes != null && totalBytes > 0) {
                        val percent = (bytesSent * 100) / totalBytes
                        if (percent % 25 == 0L) {
                            println("Upload progress: $percent%")
                        }
                    }
                }
            }
            if (response.status.isSuccess()) {
                println("Bundle successfully uploaded to Maven Central!")
            } else {
                Errors.internal.error("Failed to upload bundle: ${response.status}. Body: ${response.bodyAsText()}")
            }
        }
    }
}
