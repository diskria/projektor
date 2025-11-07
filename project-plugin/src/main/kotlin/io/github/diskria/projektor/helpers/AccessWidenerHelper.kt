package io.github.diskria.projektor.helpers

object AccessWidenerHelper {

    val TEMPLATE: String by lazy {
        buildString {
            appendLine("accessWidener v2 named")
            appendLine()
            appendLine("# region class")
            appendLine()
            appendLine("# endregion class")
            appendLine()
            appendLine("# region method")
            appendLine()
            appendLine("# endregion method")
            appendLine()
            appendLine("# region field")
            appendLine()
            appendLine("# endregion field")
            appendLine()
        }
    }
}
