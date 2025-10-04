package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.generics.joinToString

sealed class Vcs(val name: String) {

    fun buildUri(vararg parts: String): String =
        listOf("scm", name, *parts).joinToString(Constants.Char.COLON)
}

data object Git : Vcs("git")
