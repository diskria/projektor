package io.github.diskria.projektor.common.minecraft.loaders

enum class ModLoaderFamily(val loaders: List<ModLoaderType>) {

    FABRIC(
        listOf(
            ModLoaderType.FABRIC,
            ModLoaderType.LEGACY_FABRIC,
            ModLoaderType.ORNITHE,
        )
    ),
    FORGE(
        listOf(
            ModLoaderType.FORGE,
            ModLoaderType.NEOFORGE,
        )
    );

    companion object {
        fun of(loader: ModLoaderType): ModLoaderFamily =
            ModLoaderFamily.values().first { it.loaders.contains(loader) }
    }
}
