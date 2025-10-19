package io.github.diskria.projektor.requests.github.common

abstract class GithubJsonRequest : GithubRequest {
    abstract fun toJson(): String
}
