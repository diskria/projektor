package io.github.diskria.projektor.readme.shields.simple

import io.github.diskria.projektor.common.licenses.License

class LicenseShield(val license: License) : SimpleShield(
    label = "License",
    message = license.id,
    color = "yellow",
    url = license.url,
)
