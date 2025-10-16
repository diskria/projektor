package io.github.diskria.projektor.minecraft

enum class ModSide(
    val sourceSet: SourceSet,
    val minMemoryGigabytes: Int,
    val maxMemoryGigabytes: Int,
) {
    CLIENT(SourceSet.CLIENT, 2, 4),
    SERVER(SourceSet.SERVER, 4, 8),
}

fun ModSide.getMinMemoryJvmArgument(): String =
    "-Xmx${minMemoryGigabytes}G"

fun ModSide.getMaxMemoryJvmArgument(): String =
    "-Xmx${maxMemoryGigabytes}G"
