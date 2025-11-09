package io.github.diskria.projektor.minecraft.configs.fabric

import io.github.diskria.kotlin.utils.serialization.annotations.EncodeDefaults
import io.github.diskria.kotlin.utils.serialization.annotations.PrettyPrint
import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
@EncodeDefaults
@PrettyPrint
class FabricModConfig private constructor(
    val schemaVersion: Int,
    val id: String,
    val version: String,
    val name: String,
    val description: String,

    @SerialName("authors")
    val developers: List<String>,

    @SerialName("license")
    val licenseId: String,

    @SerialName("icon")
    val iconPath: String,

    val environment: String,

    @SerialName("mixins")
    val mixinsConfigPath: List<String>,

    @SerialName("accessWidener")
    val accessWidenerPath: String,

    @SerialName("contact")
    val links: FabricModLinksConfig,

    @SerialName("entrypoints")
    val entryPoints: FabricModEntryPointsConfig,

    @SerialName("depends")
    val dependencies: FabricModDependenciesConfig,
) {
    companion object {
        fun of(mod: MinecraftMod): FabricModConfig =
            FabricModConfig(
                schemaVersion = 1,
                id = mod.id,
                version = mod.version,
                name = mod.name,
                description = mod.description,
                developers = listOf(mod.repo.owner.developer),
                licenseId = mod.license.id,
                iconPath = mod.iconPath,
                environment = mod.configEnvironment,
                accessWidenerPath = mod.accessorConfigPath,
                mixinsConfigPath = listOf(mod.mixinsConfigPath),
                links = FabricModLinksConfig.of(mod),
                entryPoints = FabricModEntryPointsConfig.of(mod),
                dependencies = FabricModDependenciesConfig.of(mod),
            )
    }
}
