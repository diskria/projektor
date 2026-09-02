package io.github.diskria.projektor.internal.network.github.common

import io.ktor.http.*

internal interface GithubGetRequest : GithubRepoRequest {
    override fun getHttpMethod(): HttpMethod = HttpMethod.Get
}
