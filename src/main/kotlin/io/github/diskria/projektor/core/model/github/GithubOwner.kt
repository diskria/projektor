package io.github.diskria.projektor.core.model.github

internal data class GithubOwner(val name: String, val email: String = "") {
    val developer: String get() = name.substringBefore('-')
    val profileUrl: String get() = "https://github.com/$developer"
    val organizationUrl: String? get() = if (name.contains('-')) "https://github.com/$name" else null
    val namespace: String get() = "io.github.$developer"
}
