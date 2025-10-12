package io.github.diskria.projektor.readme.shields

import io.github.diskria.projektor.common.licenses.License

class LicenseShield(val license: License) : StaticShield(
    label = "License",
    message = license.id,
    color = "yellow",
    url = license.url,
)
