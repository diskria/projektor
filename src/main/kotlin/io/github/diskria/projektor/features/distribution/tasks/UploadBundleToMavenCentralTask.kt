package io.github.diskria.projektor.features.distribution.tasks

import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.internal.utils.Envs
import io.github.diskria.projektor.internal.utils.ProjektorHttpClient
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.cio.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.bundling.Zip
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject
import kotlin.io.encoding.Base64

@DisableCachingByDefault(because = "Uploads a ZIP bundle to Maven Central via API")
internal abstract class UploadBundleToMavenCentralTask @Inject constructor(private val envs: Envs) : Zip() {

    @get:Input
    abstract val bundleName: Property<String>

    @get:Input
    abstract val bundleVersion: Property<String>

    init {
        applyProjektorGroup()
        archiveBaseName.set(bundleName)
        archiveVersion.set(bundleVersion)
        doLast { upload() }
    }

    private fun upload() {
        if (!envs.isCI) return
        runBlocking { uploadBundle(archiveFile.get().asFile) }
    }

    private suspend fun uploadBundle(file: File) {
        val item = PartData.FileItem(
            provider = { file.readChannel() },
            dispose = {},
            partHeaders = Headers.build {
                append(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition(ContentType.MultiPart.FormData.contentSubtype).apply {
                        withParameter(ContentDisposition.Parameters.Name, "bundle")
                        withParameter(ContentDisposition.Parameters.FileName, file.name)
                    }
                )
                append(HttpHeaders.ContentType, ContentType.Application.OctetStream)
            }
        )
        val url = "https://central.sonatype.com/api/v1/publisher/upload?publishingType=AUTOMATIC"
        val token = envs.sonatypeUsername + ":" + envs.sonatypePassword
        ProjektorHttpClient.client.post(url) {
            bearerAuth(Base64.encode(token.toByteArray()))
            setBody(MultiPartFormDataContent(listOf(item)))
        }
    }
}
