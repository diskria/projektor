package io.github.diskria.projektor.common.projekt.metadata.github

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.projektor.common.projekt.OwnerType

data class GithubOwner(val type: OwnerType, val name: String) {

    val developerName: String = name.lowercase().modifyIf(type == OwnerType.DOMAIN) {
        it.substringBefore(Constants.Char.HYPHEN)
    }
}
