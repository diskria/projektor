package io.github.diskria.projektor.minecraft.utils

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.ktor.http.*

object ModrinthUtils {

    fun getModUrl(slug: String): String =
        buildUrl("modrinth.com") {
            path("mod", slug)
        }
}
