package io.github.diskria.projektor.tasks.generate

import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.BracketsType
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.serialization.deserialize
import io.github.diskria.kotlin.utils.extensions.wrapWithBrackets
import io.github.diskria.projektor.ProjektBuildConfig
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.getHomepages
import io.github.diskria.projektor.requests.github.GetLanguagesRequest
import io.github.diskria.projektor.requests.github.UpdateInfoRequest
import io.github.diskria.projektor.requests.github.UpdateTopicsRequest
import io.github.diskria.projektor.requests.github.common.GithubJsonRequest
import io.github.diskria.projektor.requests.github.common.GithubRequest
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
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
        group = ProjektorGradlePlugin.TASK_GROUP

        metadata.convention(project.getProjektMetadata())
    }

    @TaskAction
    fun update() {
        println("[UpdateGithubRepositoryMetadataTask] start")
        if (!EnvironmentHelper.isCI()) {
            println("[UpdateGithubRepositoryMetadataTask] not running on CI, stop")
            return
        }
        runBlocking {
            updateInfo()
            updateTopics()
        }
        println("[UpdateGithubRepositoryMetadataTask] end")
    }

    private suspend fun updateInfo() {
        with(metadata.get()) {
            sendRequest(
                UpdateInfoRequest(repo.name, description, getHomepages().first())
            )
        }
    }

    private suspend fun updateTopics() {
        val metadata = metadata.get()

        val topics = buildSet {
            getTopLanguage()?.let { add(it) }
            add(metadata.type.getName(`kebab-case`))
            addAll(metadata.tags)
            add(metadata.publishingTargets.first().getName(`kebab-case`))
        }
        sendRequest(UpdateTopicsRequest(topics.toList()))
    }

    private suspend fun getTopLanguage(): String? =
        sendRequest(GetLanguagesRequest()).bodyAsText().deserialize<Map<String, Int>>().maxByOrNull { it.value }?.key

    private suspend fun sendRequest(request: GithubRequest): HttpResponse {
        HttpClient(CIO).use { client ->
            val url = buildUrl("api.github.com") {
                val repo = metadata.get().repo
                path("repos", repo.owner.name, repo.name, *request.getExtraPathSegments().toTypedArray())
            }
            return client.request(url) {
                method = request.getHttpMethod()
                bearerAuth(Secrets.githubToken)
                header(
                    HttpHeaders.UserAgent,
                    buildString {
                        append(ProjektBuildConfig.PLUGIN_NAME)
                        append(Constants.Char.SLASH)
                        append(ProjektBuildConfig.PLUGIN_VERSION)
                        append(Constants.Char.SPACE)
                        append(
                            buildString {
                                append(Constants.Char.PLUS)
                                append(metadata.get().repo.getUrl())
                            }.wrapWithBrackets(BracketsType.ROUND)
                        )
                    }
                )
                header(HttpHeaders.Accept, "application/vnd.github+json")
                if (request is GithubJsonRequest) {
                    contentType(ContentType.Application.Json)
                    setBody(request.toJson())
                }
            }.also {
                println("[UpdateGithubRepositoryMetadataTask] response = ${it.bodyAsText()}")
            }
        }
    }
}
