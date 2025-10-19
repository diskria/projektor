package io.github.diskria.projektor.requests.github.common

import io.ktor.http.*

interface GithubRequest {
    fun getHttpMethod(): HttpMethod
    fun getExtraPathSegments(): List<String> = emptyList()
}
