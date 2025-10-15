package io.github.diskria.projektor.tasks.release

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.publishing.maven.MavenCentral
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.core.writeFully
import kotlinx.coroutines.runBlocking
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.bundling.Zip
import kotlin.io.encoding.Base64
import kotlin.io.use
import kotlin.text.toByteArray

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
        val archiveFile = archiveFile.get().asFile
        val bearer = Base64.encode("${Secrets.sonatypeUsername}:${Secrets.sonatypePassword}".toByteArray())
        val url = buildUrl("central.sonatype.com", URLProtocol.HTTPS) {
            path("api", "v1", "publisher", "upload")
            parameters.append("publishingType", "AUTOMATIC")
        }
        val form = formData {
            append("bundle", archiveFile.name, ContentType.Application.Zip) {
                writeFully(archiveFile.readBytes())
            }
        }
        HttpClient(CIO).use { client ->
            client.submitFormWithBinaryData(url, form) {
                method = HttpMethod.Post
                headers {
                    append(HttpHeaders.Authorization, "Bearer $bearer")
                }
            }
        }
    }
}
