package io.github.diskria.projektor.settings.projekt.common

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.projektor.settings.licenses.License

interface IProjekt {
    val owner: String
    val repo: String
    val description: String
    val version: String
    val license: License
    val tags: Set<String>

    val developer: String
        get() = owner.substringBefore(Constants.Char.HYPHEN)

    val name: String
        get() = repo.setCase(`kebab-case`, `Title Case`)
}
