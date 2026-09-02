package io.github.diskria.projektor.internal.network.github.common

internal interface GithubJsonRequest : GithubRepoRequest {
    fun toJson(): String
}
