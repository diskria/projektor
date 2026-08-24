package io.github.diskria.projektor.features.generation.readme.shields.static

import io.github.diskria.projektor.core.model.license.License

internal class LicenseShield(val license: License) : StaticShield(license.id, "yellow") {
    override fun getLabel(): String = "License"
    override fun getUrl(): String = license.url
}
