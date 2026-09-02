package io.github.diskria.projektor.internal.network.github

import io.github.diskria.projektor.internal.network.github.common.GithubGetRequest

internal class GetLanguagesRequest : GithubGetRequest {
    override fun getPathSegment() = "languages"
}
