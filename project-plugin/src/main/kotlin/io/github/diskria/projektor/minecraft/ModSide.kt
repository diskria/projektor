package io.github.diskria.projektor.minecraft

enum class ModSide(
    val sourceSet: SourceSet,
    val maxMemoryGigabytes: Int,
) {
    CLIENT(SourceSet.CLIENT, 4),
    SERVER(SourceSet.SERVER, 8),
}

fun ModSide.getMaxMemoryJvmArgument(): String =
    "-Xmx${maxMemoryGigabytes}G"

fun ModSide.logicalName(): String =
    name.lowercase()
