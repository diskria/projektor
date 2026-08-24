package io.github.diskria.projektor.internal.utils

internal fun String.capitalized(): String =
    replaceFirstChar { if (it.isLowerCase()) it.uppercase() else it.toString() }

internal fun String.decapitalized(): String =
    replaceFirstChar { it.lowercase() }
