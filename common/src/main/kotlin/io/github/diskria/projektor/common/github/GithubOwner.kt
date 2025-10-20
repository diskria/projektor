package io.github.diskria.projektor.common.github

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.projektor.common.projekt.OwnerType
import io.github.diskria.projektor.common.repository.RepositoryHost

data class GithubOwner(val name: String, val email: String) {

    val type: OwnerType
        get() = when {
            name.first().isUpperCase() -> OwnerType.BRAND
            name.contains(Constants.Char.HYPHEN) -> OwnerType.DOMAIN
            else -> OwnerType.PROFILE
        }

    val developer: String
        get() = name.lowercase().modifyIf(type == OwnerType.DOMAIN) { it.substringBefore(Constants.Char.HYPHEN) }

    val namespace: String
        get() = "io".appendPackageName(RepositoryHost.GITHUB.shortName).appendPackageName(developer)
}
