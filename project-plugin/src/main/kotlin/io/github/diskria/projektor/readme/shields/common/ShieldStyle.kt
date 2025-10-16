package io.github.diskria.projektor.readme.shields.common

import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName

enum class ShieldStyle {
    FOR_THE_BADGE;
}

fun ShieldStyle.getParameterName(): String =
    getName(`kebab-case`)
