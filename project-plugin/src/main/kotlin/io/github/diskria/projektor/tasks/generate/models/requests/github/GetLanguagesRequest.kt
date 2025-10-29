package io.github.diskria.projektor.tasks.generate.models.requests.github

import io.github.diskria.projektor.tasks.generate.models.requests.github.common.GithubGetRequest

class GetLanguagesRequest : GithubGetRequest() {
    override fun getExtraPathSegments(): List<String> = listOf("languages")
}
