package io.github.diskria.projektor.helpers

import io.github.diskria.kotlin.utils.extensions.common.fileName

object AccessWidenerHelper {

    const val HEADER: String = "accessWidener v2 named"

    fun getFileName(modId: String): String =
        fileName(modId, "accesswidener")
}
