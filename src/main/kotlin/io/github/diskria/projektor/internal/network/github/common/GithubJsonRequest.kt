package io.github.diskria.projektor.internal.network.github.common

internal abstract class GithubJsonRequest : GithubRepoRequest {
    abstract fun toJson(): String
}
