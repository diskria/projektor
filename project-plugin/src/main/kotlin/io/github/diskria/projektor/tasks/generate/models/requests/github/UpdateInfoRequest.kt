package io.github.diskria.projektor.tasks.generate.models.requests.github

import io.github.diskria.kotlin.utils.extensions.serialization.serializeToJson
import io.github.diskria.projektor.tasks.generate.models.requests.github.common.GithubJsonRequest
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateInfoRequest(
    val name: String,
    val description: String,

    @SerialName("homepage")
    val homepageUrl: String? = null
) : GithubJsonRequest() {
    override fun getHttpMethod(): HttpMethod = HttpMethod.Patch
    override fun toJson(): String = serializeToJson()
}
