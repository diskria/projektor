package io.github.diskria.projektor.features.metadata.tasks

import io.github.diskria.projektor.core.model.ProjektType
import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.core.model.metadata.ProjektAbout
import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.generated.EnvProvider
import io.github.diskria.projektor.internal.network.github.GetLanguagesRequest
import io.github.diskria.projektor.internal.network.github.UpdateInfoRequest
import io.github.diskria.projektor.internal.network.github.UpdateTopicsRequest
import io.github.diskria.projektor.internal.network.github.common.GithubJsonRequest
import io.github.diskria.projektor.internal.network.github.common.GithubRepoRequest
import io.github.diskria.projektor.internal.utils.DisabledCachingReasons.NON_DETERMINISTIC
import io.github.diskria.projektor.internal.utils.DisabledCachingReasons.SIDE_EFFECTS
import io.github.diskria.projektor.internal.utils.ProjektorHttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault(because = "$NON_DETERMINISTIC; $SIDE_EFFECTS")
abstract class UpdateGithubRepoMetadataTask @Inject internal constructor(
    private val providers: ProviderFactory,
) : DefaultTask() {

    @get:Input
    abstract val projektTypes: ListProperty<ProjektType>

    @get:Input
    abstract val about: Property<ProjektAbout>

    @get:Input
    abstract val repo: Property<GithubRepo>

    @get:Optional
    @get:Input
    abstract val homepageUrl: Property<String>

    init {
        applyProjektorGroup()
    }

    @TaskAction
    fun update() {
        val env = EnvProvider(providers)
        if (!env.isCI) return
        runBlocking {
            updateInfo(env)
            updateTopics(env)
        }
    }

    private suspend fun updateInfo(env: EnvProvider) {
        sendRequest(
            UpdateInfoRequest(
                name = repo.get().name,
                description = about.get().description,
                homepageUrl = homepageUrl.orNull,
            ),
            env
        )
    }

    private suspend fun updateTopics(env: EnvProvider) {
        val topics = buildSet {
            getTopLanguage(env)?.let { add(it) }
            addAll(projektTypes.get().map { it.id })
            addAll(about.get().tags)
        }
        sendRequest(UpdateTopicsRequest(topics.toList()), env)
    }

    private suspend fun getTopLanguage(env: EnvProvider): String? {
        val languages = Json.decodeFromString<Map<String, Int>>(sendRequest(GetLanguagesRequest(), env).bodyAsText())
        return languages.maxByOrNull { it.value }?.key
    }

    private suspend fun sendRequest(request: GithubRepoRequest, env: EnvProvider): HttpResponse {
        val url = buildString {
            append("https://api.github.com/repos/${repo.get().owner.name}/${repo.get().name}")
            request.getPathSegment()?.let { append("/$it") }
        }
        return ProjektorHttpClient.client.request(url) {
            method = request.getHttpMethod()
            bearerAuth(env.githubToken)
            header(HttpHeaders.Accept, "application/vnd.github+json")
            if (request is GithubJsonRequest) {
                contentType(ContentType.Application.Json)
                setBody(request.toJson())
            }
        }
    }
}
