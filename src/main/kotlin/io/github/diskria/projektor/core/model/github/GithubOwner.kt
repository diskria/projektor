package io.github.diskria.projektor.core.model.github

import kotlinx.serialization.Serializable

@Serializable
internal data class GithubOwner(val name: String, val email: String = "") {
    val profileUrl: String get() = "https://github.com/${name.substringBefore('-')}"
    val organizationUrl: String? get() = if (name.contains('-')) "https://github.com/$name" else null
    val developer: String get() = name.lowercase().substringBefore('-')
    val namespace: String get() = "io.github.$developer"
}
