package io.github.diskria.projektor.core.model.github

import java.io.Serializable
import kotlinx.serialization.Serializable as KotlinxSerializable

@KotlinxSerializable
internal class GithubOwner(val name: String, val email: String = "") : Serializable {
    val developer: String get() = name.substringBefore('-')
    val profileUrl: String get() = "https://github.com/$developer"
    val organizationUrl: String? get() = if (name.contains('-')) "https://github.com/$name" else null
    val namespace: String get() = "io.github.$developer"
}
