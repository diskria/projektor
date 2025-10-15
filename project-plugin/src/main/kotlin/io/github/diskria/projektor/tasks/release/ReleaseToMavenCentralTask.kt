package io.github.diskria.projektor.tasks.release

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.extensions.wrapWithDoubleQuote
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.publishing.maven.MavenCentral
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.bundling.Zip
import kotlin.io.encoding.Base64

abstract class ReleaseToMavenCentralTask : Zip() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:InputDirectory
    abstract val localMavenDirectory: DirectoryProperty

    init {
        dependsOn(MavenCentral.getConfigurePublicationTaskName())
        archiveBaseName.set(metadata.map { it.repo })
        archiveVersion.set(metadata.map { it.version })
        destinationDirectory.set(
            project.getBuildDirectory(MavenCentral.getTypeName().setCase(PascalCase, `kebab-case`))
        )
        from(localMavenDirectory)
        doLast {
            runBlocking {
                uploadBundleToSonatype()
            }
        }
    }

    suspend fun uploadBundleToSonatype() {
        val bundleFile = archiveFile.get().asFile

        val bearer = Base64.encode("${Secrets.sonatypeUsername}:${Secrets.sonatypePassword}".toByteArray())
        val url = buildUrl("central.sonatype.com", URLProtocol.HTTPS) {
            path("api", "v1", "publisher", "upload")
            parameters.append("publishingType", "AUTOMATIC")
        }

        val boundary = "----WebKitFormBoundary${System.currentTimeMillis()}"
        val lineBreak = "\r\n"
        val doubleDash = "--"

        val disposition = buildString {
            append("form-data; ")
            append("name=")
            append("bundle".wrapWithDoubleQuote())
            append("; ")
            append("filename=")
            append(bundleFile.name.wrapWithDoubleQuote())
        }

        val bodyPrefix = buildString {
            append(doubleDash)
            append(boundary)
            append(lineBreak)
            append("${HttpHeaders.ContentDisposition}: $disposition")
            append(lineBreak)
            append("${HttpHeaders.ContentType}: ${ContentType.Application.OctetStream}")
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

        val body = bodyPrefix + bundleFile.readBytes() + bodySuffix
        HttpClient(CIO).use { client ->
            client.post(url) {
                header(HttpHeaders.Authorization, "Bearer $bearer")
                header(
                    HttpHeaders.ContentType,
                    ContentType.MultiPart.FormData.withParameter("boundary", boundary).toString()
                )
                setBody(body)
            }
        }
    }
}
