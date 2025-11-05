package io.github.diskria.projektor.readme.shields.static

import io.github.diskria.projektor.licenses.License
import io.ktor.http.*

class LicenseShield(val license: License) : StaticShield(license.id, "yellow") {

    override fun getLabel(): String =
        "License"

    override fun getUrl(): Url =
        license.url
}
