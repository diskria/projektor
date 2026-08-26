package io.github.diskria.projektor.features.metadata.tasks

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.extensions.isCI
import io.github.diskria.projektor.internal.network.github.GetLanguagesRequest
import io.github.diskria.projektor.internal.network.github.UpdateInfoRequest
import io.github.diskria.projektor.internal.network.github.UpdateTopicsRequest
import io.github.diskria.projektor.internal.network.github.common.GithubJsonRequest
import io.github.diskria.projektor.internal.network.github.common.GithubRepoRequest
import io.github.diskria.projektor.internal.utils.ProjektorHttpClient
import io.github.diskria.projektor.internal.utils.SecretsHelper
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault(because = "Updates external GitHub repository metadata via API; must always reflect current project state")
internal abstract class UpdateGithubRepoMetadataTask @Inject constructor(
    private val providers: ProviderFactory,
    private val secrets: SecretsHelper,
) : DefaultTask() {

    @get:Internal
    abstract val primaryProjekt: Property<Projekt>

    init {
        applyProjektorGroup()
    }

    @TaskAction
    fun update() {
        if (!providers.isCI) return
        runBlocking {
            updateInfo()
            updateTopics()
        }
    }

    private suspend fun updateInfo() {
        val primaryProjekt = primaryProjekt.get()
        sendRequest(
            UpdateInfoRequest(
                name = primaryProjekt.metadata.repo.name,
                description = primaryProjekt.metadata.description,
                homepageUrl = primaryProjekt.distributionTargets.firstOrNull()?.getHomepage(primaryProjekt),
            )
        )
    }

    private suspend fun updateTopics() {
        val metadata = primaryProjekt.get().metadata
        val topics = buildSet {
            getTopLanguage()?.let { add(it) }
            addAll(metadata.projektTypes.map { it.topicName })
            addAll(metadata.tags)
        }
        sendRequest(UpdateTopicsRequest(topics.toList()))
    }

    private suspend fun getTopLanguage(): String? {
        val languages: Map<String, Int> = Json.decodeFromString(sendRequest(GetLanguagesRequest()).bodyAsText())
        return languages.maxByOrNull { it.value }?.key
    }

    private suspend fun sendRequest(request: GithubRepoRequest): HttpResponse {
        val repo = primaryProjekt.get().metadata.repo
        val url = buildString {
            append("https://api.github.com/repos/${repo.owner.name}/${repo.name}")
            request.getPathSegment()?.let { append("/$it") }
        }
        return ProjektorHttpClient.client.request(url) {
            method = request.getHttpMethod()
            bearerAuth(secrets.githubToken)
            header(HttpHeaders.Accept, "application/vnd.github+json")
            if (request is GithubJsonRequest) {
                contentType(ContentType.Application.Json)
                setBody(request.toJson())
            }
        }
    }
}
