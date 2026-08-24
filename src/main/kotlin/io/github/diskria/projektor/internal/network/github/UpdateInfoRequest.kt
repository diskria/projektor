package io.github.diskria.projektor.internal.network.github

import io.github.diskria.projektor.internal.network.github.common.GithubJsonRequest
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class UpdateInfoRequest(
    val name: String,
    val description: String,
    @SerialName("homepage") val homepageUrl: String? = null,
) : GithubJsonRequest() {
    override fun getHttpMethod(): HttpMethod = HttpMethod.Patch
    override fun toJson(): String = Json.encodeToString(this)
}
