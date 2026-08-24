package io.github.diskria.projektor.internal.network.github.common

import io.ktor.http.*

internal open class GithubGetRequest : GithubRepoRequest {
    override fun getHttpMethod(): HttpMethod = HttpMethod.Get
}
