package io.github.diskria.projektor.minecraft.utils

import io.github.diskria.utils.kotlin.extensions.common.buildUrl
import io.ktor.http.*

object ModrinthUtils {

    fun getProjectUrl(id: String): String =
        buildUrl("modrinth.com") {
            path("project", id)
        }
}
