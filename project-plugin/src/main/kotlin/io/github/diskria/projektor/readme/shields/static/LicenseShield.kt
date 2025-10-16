package io.github.diskria.projektor.readme.shields.static

import io.github.diskria.projektor.licenses.License

class LicenseShield(val license: License) : StaticShield(
    label = "License",
    message = license.id,
    color = "yellow",
    url = license.url,
)
