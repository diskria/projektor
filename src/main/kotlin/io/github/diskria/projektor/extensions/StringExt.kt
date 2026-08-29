package io.github.diskria.projektor.extensions

internal fun String.capitalized(): String =
    replaceFirstChar { if (it.isLowerCase()) it.uppercase() else it.toString() }

@PublishedApi
internal fun String.decapitalized(): String =
    replaceFirstChar { it.lowercase() }
