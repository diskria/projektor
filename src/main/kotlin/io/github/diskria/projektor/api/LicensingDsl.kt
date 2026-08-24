package io.github.diskria.projektor.api

import io.github.diskria.projektor.core.model.license.LicenseType

class LicensingDsl internal constructor(private val onSelect: (LicenseType) -> Unit) : ProjektorScope {

    fun mit() {
        onSelect(LicenseType.MIT)
    }
}
