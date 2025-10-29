package io.github.diskria.projektor.tasks.generate.models.requests.github.common

import io.ktor.http.*

interface GithubRequest {
    fun getHttpMethod(): HttpMethod
    fun getExtraPathSegments(): List<String> = emptyList()
}
