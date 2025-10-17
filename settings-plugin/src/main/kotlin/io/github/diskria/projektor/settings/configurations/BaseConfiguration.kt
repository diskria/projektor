package io.github.diskria.projektor.settings.configurations

import io.github.diskria.projektor.common.projekt.ProjektType

open class BaseConfiguration() {
    var versionCatalogPath: String? = null
    var extraRepositories: MutableSet<ProjektType> = mutableSetOf()
}
