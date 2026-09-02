package io.github.diskria.projektor.features.distribution.tasks

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.features.distribution.target.MavenCentralDistributionTarget
import io.github.diskria.projektor.generated.EnvProvider
import io.github.diskria.projektor.internal.utils.DisabledCachingReasons.SIDE_EFFECTS
import io.github.diskria.projektor.internal.utils.ProjektorHttpClient
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.cio.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.bundling.Zip
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject
import kotlin.io.encoding.Base64

@DisableCachingByDefault(because = SIDE_EFFECTS)
abstract class UploadBundleToMavenCentralTask @Inject constructor(
    private val env: EnvProvider,
    layout: ProjectLayout,
) : Zip() {

    @get:Input
    abstract val bundleName: Property<String>

    @get:Input
    abstract val bundleVersion: Property<String>

    init {
        applyProjektorGroup()
        archiveBaseName.set(bundleName)
        archiveVersion.set(bundleVersion)
        from(MavenCentralDistributionTarget.getLocalMavenDirectory(layout))
        destinationDirectory.set(layout.buildDirectory.dir(DistributionTargetType.MAVEN_CENTRAL.id))
        doLast {
            if (!env.isCI) return@doLast
            runBlocking { upload(archiveFile.get().asFile) }
        }
    }

    private suspend fun upload(file: File) {
        val deploymentName = file.name
        logger.lifecycle("Uploading bundle '$deploymentName' to Maven Central...")
        val item = PartData.FileItem(
            provider = { file.readChannel() },
            dispose = {},
            partHeaders = Headers.build {
                append(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition(ContentType.MultiPart.FormData.contentSubtype)
                        .withParameter(ContentDisposition.Parameters.Name, "bundle")
                        .withParameter(ContentDisposition.Parameters.FileName, file.name)
                )
                append(HttpHeaders.ContentType, ContentType.Application.OctetStream)
            }
        )
        val url = "https://central.sonatype.com/api/v1/publisher/upload?publishingType=AUTOMATIC"
        val token = "${env.sonatypeUsername}:${env.sonatypePassword}"
        val response = ProjektorHttpClient.client.post(url) {
            bearerAuth(Base64.encode(token.toByteArray()))
            setBody(MultiPartFormDataContent(listOf(item)))
        }
        val responseText = response.bodyAsText()
        if (response.status.isSuccess()) {
            logger.lifecycle("Bundle '$deploymentName' uploaded successfully. Response: $responseText")
        } else {
            logger.error("Failed to upload bundle '$deploymentName': $responseText")
        }
    }
}
