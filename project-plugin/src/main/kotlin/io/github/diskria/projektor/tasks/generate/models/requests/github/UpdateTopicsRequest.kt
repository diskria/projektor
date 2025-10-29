package io.github.diskria.projektor.tasks.generate.models.requests.github

import io.github.diskria.kotlin.utils.extensions.serialization.serializeToJson
import io.github.diskria.projektor.tasks.generate.models.requests.github.common.GithubJsonRequest
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class UpdateTopicsRequest(val names: List<String>) : GithubJsonRequest() {
    override fun getHttpMethod(): HttpMethod = HttpMethod.Put
    override fun toJson(): String = serializeToJson()
    override fun getExtraPathSegments(): List<String> = listOf("topics")
}
