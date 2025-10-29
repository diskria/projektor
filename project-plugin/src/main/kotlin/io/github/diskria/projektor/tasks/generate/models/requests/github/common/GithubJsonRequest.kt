package io.github.diskria.projektor.tasks.generate.models.requests.github.common

abstract class GithubJsonRequest : GithubRequest {
    abstract fun toJson(): String
}
