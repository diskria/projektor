package io.github.diskria.projektor.requests.github

import io.github.diskria.kotlin.utils.extensions.serialization.serialize
import io.github.diskria.projektor.requests.github.common.IGithubRequest
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class UpdateTopicsRequest(val names: List<String>) : IGithubRequest {
    override fun getHttpMethod(): HttpMethod = HttpMethod.Put
    override fun toJson(): String = serialize()
    override fun getExtraPathSegments(): List<String> = listOf("topics")
}
