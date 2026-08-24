package io.github.diskria.projektor.internal.network.github

import io.github.diskria.projektor.internal.network.github.common.GithubJsonRequest
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class UpdateTopicsRequest(val names: List<String>) : GithubJsonRequest() {
    override fun getHttpMethod(): HttpMethod = HttpMethod.Put
    override fun toJson(): String = Json.encodeToString(this)
    override fun getPathSegment() = "topics"
}
