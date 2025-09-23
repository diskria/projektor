package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.Constants

enum class SoftwareForgeType(val displayName: String, val hostname: String, val scmType: ScmType) {
    GITHUB("GitHub", "github.com", ScmType.GIT),
}

fun SoftwareForgeType.getSshAuthority(): String =
    scmType.logicalName() + Constants.Char.AT_SIGN + hostname
