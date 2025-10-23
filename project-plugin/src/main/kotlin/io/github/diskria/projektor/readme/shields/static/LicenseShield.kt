package io.github.diskria.projektor.readme.shields.static

import io.github.diskria.projektor.licenses.License

class LicenseShield(val license: License) : StaticShield(license.id, "yellow") {

    override fun getLabel(): String =
        "License"

    override fun getUrl(): String =
        license.url
}
