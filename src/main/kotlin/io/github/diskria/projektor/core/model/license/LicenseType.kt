package io.github.diskria.projektor.core.model.license

import io.github.diskria.projektor.core.model.license.LicenseType.MIT

internal enum class LicenseType {
    MIT,
}

internal fun LicenseType.mapToModel(): License =
    when (this) {
        MIT -> Mit
    }

internal fun License.mapToEnum(): LicenseType =
    when (this) {
        Mit -> MIT
    }
