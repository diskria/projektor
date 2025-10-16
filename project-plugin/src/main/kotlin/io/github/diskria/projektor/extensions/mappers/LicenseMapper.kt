package io.github.diskria.projektor.extensions.mappers

import io.github.diskria.projektor.common.licenses.LicenseType
import io.github.diskria.projektor.common.licenses.LicenseType.MIT
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.licenses.Mit

fun LicenseType.mapToModel(): License =
    when (this) {
        MIT -> Mit
    }

fun License.mapToEnum(): LicenseType =
    when (this) {
        Mit -> MIT
    }
