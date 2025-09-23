package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.generics.joinToString

enum class ScmType {
    GIT,
}

fun ScmType.logicalName(): String =
    name.lowercase()

fun ScmType.buildUri(vararg parts: String): String =
    listOf("scm", logicalName(), *parts).joinToString(Constants.Char.COLON)
