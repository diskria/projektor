package io.github.diskria.projektor.internal.network.github

import io.github.diskria.projektor.internal.network.github.common.GithubJsonRequest
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable as JsonSerializable

@JsonSerializable
internal data class UpdateTopicsRequest(val names: List<String>) : GithubJsonRequest {
    override fun getHttpMethod(): HttpMethod = HttpMethod.Put
    override fun toJson(): String = Json.encodeToString(this)
    override fun getPathSegment() = "topics"
}
