package io.github.diskria.projektor.internal.network.github.common

import io.ktor.http.*

internal interface GithubRepoRequest {
    fun getHttpMethod(): HttpMethod
    fun getPathSegment(): String? = null
}
