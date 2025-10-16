package io.github.diskria.projektor.common.projekt.metadata.github

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.projektor.common.projekt.OwnerType
import io.github.diskria.projektor.common.repository.RepositoryHost

data class GithubOwner(val type: OwnerType, val name: String, val email: String) {

    val namespace: String
        get() = "io".appendPackageName(RepositoryHost.GITHUB.shortName).appendPackageName(developerName)

    val developerName: String = name.lowercase().modifyIf(type == OwnerType.DOMAIN) {
        it.substringBefore(Constants.Char.HYPHEN)
    }
}
