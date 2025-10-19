package io.github.diskria.projektor.requests.github.common

import io.ktor.http.*

open class GithubGetRequest : GithubRequest {
    override fun getHttpMethod(): HttpMethod = HttpMethod.Get
}
