package io.github.diskria.projektor.utils

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName

object VersionCatalogUtils {

    const val DEFAULT_CATALOG_NAME = "libs"
    const val CATALOG_VERSIONS = "versions"

    fun buildCatalogFileName(catalogName: String): String =
        fileName(catalogName, "versions", Constants.File.Extension.TOML)
}
