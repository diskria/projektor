package io.github.diskria.projektor.core.model.license

import io.github.diskria.projektor.core.model.license.LicenseType.MIT

enum class LicenseType(val id: String) {
    MIT("MIT"),
}

internal fun LicenseType.mapToModel(): License = when (this) {
    MIT -> Mit
}
