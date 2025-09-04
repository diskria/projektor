package io.github.diskria.projektor.minecraft

enum class ModEnvironment(
    val fabricConfigValue: String,
    val sides: List<ModSide>,
) {
    CLIENT_SERVER(
        "*",
        listOf(ModSide.CLIENT, ModSide.SERVER)
    ),
    CLIENT_SIDE_ONLY(
        "client",
        listOf(ModSide.CLIENT)
    ),
    SERVER_SIDE_ONLY(
        "server",
        listOf(ModSide.SERVER)
    );

    fun getSourceSets(): List<SourceSet> =
        listOf(SourceSet.MAIN) + sides.map { it.sourceSet }
}
