package io.github.diskria.projektor.tasks.generate

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.Environment
import io.github.diskria.projektor.ProjektBuildConfig
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.common.publishing.PublishingTargetType.*
import io.github.diskria.projektor.extensions.getMetadata
import io.github.diskria.projektor.requests.github.UpdateInfoRequest
import io.github.diskria.projektor.requests.github.UpdateTopicsRequest
import io.github.diskria.projektor.requests.github.common.IGithubRequest
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class UpdateGithubRepositoryMetadataTask : DefaultTask() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    init {
        metadata.convention(project.getMetadata())
    }

    @TaskAction
    fun update() {
        if (!Environment.isCI()) {
            return
        }
        runBlocking {
            updateInfo()
            updateTopics()
        }
    }

    private suspend fun updateInfo() {
        val metadata = metadata.get()

        val homepage = when (metadata.publishingTarget) {
            GITHUB_PACKAGES -> null
            GITHUB_PAGES -> metadata.repository.getPagesUrl()
            MAVEN_CENTRAL -> buildUrl("central.sonatype.com") {
                path("artifact", metadata.repository.owner.namespace, metadata.repository.name)
            }

            GRADLE_PLUGIN_PORTAL -> null
            MODRINTH -> null
            GOOGLE_PLAY -> null
        }
        sendRequest(UpdateInfoRequest(metadata.repository.name, metadata.description, homepage))
    }

    private suspend fun updateTopics() {
        val metadata = metadata.get()

        val topics = buildSet {
            add(metadata.type.getName(`kebab-case`))
            addAll(metadata.tags)
            add(metadata.publishingTarget.getName(`kebab-case`))
        }
        sendRequest(UpdateTopicsRequest(topics.toList()))
    }

    private suspend fun sendRequest(request: IGithubRequest) {
        HttpClient(CIO).use { client ->
            val url = buildUrl("api.github.com") {
                val repository = metadata.get().repository
                path("repos", repository.owner.name, repository.name, *request.getExtraPathSegments().toTypedArray())
            }
            client.request(url) {
                method = request.getHttpMethod()
                bearerAuth(Environment.Secrets.githubToken)
                header(
                    HttpHeaders.UserAgent,
                    "${ProjektBuildConfig.PLUGIN_NAME}/1.0 (+${metadata.get().repository.getUrl()})"
                )
                header(HttpHeaders.Accept, "application/vnd.github+json")
                contentType(ContentType.Application.Json)
                setBody(request.toJson())
            }
        }
    }
}
