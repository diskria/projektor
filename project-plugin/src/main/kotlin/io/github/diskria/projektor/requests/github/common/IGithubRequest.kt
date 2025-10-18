package io.github.diskria.projektor.requests.github.common

import io.ktor.http.*

interface IGithubRequest {
    fun getAcceptHeader(): String
    fun getHttpMethod(): HttpMethod
    fun toJson(): String
    fun getExtraPathSegments(): List<String> = emptyList()
}
