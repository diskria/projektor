package io.github.diskria.projektor.licenses

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.ktor.http.*

sealed class License(val id: String, val displayName: String) {

    val url: String by lazy {
        buildUrl("opensource.org") {
            path("licenses", id)
        }
    }
}
