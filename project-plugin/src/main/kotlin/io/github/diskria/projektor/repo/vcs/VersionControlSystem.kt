package io.github.diskria.projektor.repo.vcs

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.generics.joinToString

sealed class VersionControlSystem(val name: String) {

    fun buildScmUri(vararg parts: String): String =
        listOf("scm", name, *parts).joinToString(Constants.Char.COLON)
}
