package io.github.diskria.projektor.core.model.github

import kotlinx.serialization.Serializable

@Serializable
internal data class GithubOwner(val name: String, val email: String = "") {
    val developer: String get() = name.lowercase().substringBefore('-')
    val namespace: String get() = "io.github.$developer"
}
