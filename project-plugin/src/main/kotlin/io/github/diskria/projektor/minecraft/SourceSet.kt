package io.github.diskria.projektor.minecraft

enum class SourceSet {
    MAIN,
    CLIENT,
    SERVER,
}

fun SourceSet.logicalName(): String =
    name.lowercase()
