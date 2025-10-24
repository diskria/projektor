package io.github.diskria.projektor.minecraft

enum class ModSide(
    val minMemoryGigabytes: Int,
    val maxMemoryGigabytes: Int,
) {
    CLIENT(2, 4),
    SERVER(4, 8);
}
