package io.github.diskria.projektor.minecraft.configs.fabric

import io.github.diskria.projektor.projekt.MinecraftMod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
class FabricModLinksConfig private constructor(
    @SerialName("homepage")
    val homepageUrl: String,

    @SerialName("sources")
    val repoUrl: String,

    @SerialName("issues")
    val issuesUrl: String,
) {
    companion object {
        fun of(mod: MinecraftMod): FabricModLinksConfig =
            FabricModLinksConfig(
                homepageUrl = mod.getModrinthUrl().toString(),
                repoUrl = mod.repo.getUrl(),
                issuesUrl = mod.repo.getIssuesUrl(),
            )
    }
}
