package io.github.diskria.projektor.requests.github

import io.github.diskria.kotlin.utils.extensions.serialization.serialize
import io.github.diskria.projektor.requests.github.common.IGithubRequest
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class UpdateInfoRequest(
    val name: String,
    val description: String,
    val homepage: String? = null,
) : IGithubRequest {
    override fun getHttpMethod(): HttpMethod = HttpMethod.Patch
    override fun toJson(): String = serialize()
}
