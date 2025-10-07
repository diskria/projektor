package io.github.diskria.projektor.licenses

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.ktor.http.*

enum class License(val id: String) {

    MIT("MIT");

    companion object {
        fun of(id: String): License =
            entries.find { it.id == id } ?: gradleError("Unknown license type: $id")
    }
}

fun License.getUrl(): String =
    buildUrl("raw.githubusercontent.com") {
        path("spdx", "license-list-data", "main", "text", fileName(id, Constants.File.Extension.TXT))
    }
