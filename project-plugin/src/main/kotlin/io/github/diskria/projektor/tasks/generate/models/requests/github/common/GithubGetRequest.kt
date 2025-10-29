package io.github.diskria.projektor.tasks.generate.models.requests.github.common

import io.ktor.http.*

open class GithubGetRequest : GithubRequest {
    override fun getHttpMethod(): HttpMethod = HttpMethod.Get
}
