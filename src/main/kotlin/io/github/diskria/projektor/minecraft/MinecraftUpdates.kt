package io.github.diskria.projektor.minecraft

import io.github.diskria.projektor.minecraft.version.MinecraftVersionRange
import io.github.diskria.projektor.minecraft.version.Release
import org.gradle.api.Incubating

object MinecraftUpdates {

    object JungleUpdate : MinecraftVersionRange(Release.V_1_2_1)

    object VillagerTradingUpdate : MinecraftVersionRange(
        Release.V_1_3_1,
        Release.V_1_3_2
    )

    object UpdateAquatic : MinecraftVersionRange(
        Release.V_1_13,
        Release.V_1_13_2
    )

    object VillageAndPillage : MinecraftVersionRange(
        Release.V_1_14,
        Release.V_1_14_4
    )

    object BuzzyBees : MinecraftVersionRange(
        Release.V_1_15,
        Release.V_1_15_2
    )

    object ArmoredPaws : MinecraftVersionRange(
        Release.V_1_20_5,
        Release.V_1_20_6
    )

    object TrickyTrials : MinecraftVersionRange(
        Release.V_1_21,
        Release.V_1_21_1
    )

    object BundlesOfBravery : MinecraftVersionRange(
        Release.V_1_21_2,
        Release.V_1_21_3
    )

    object TheGardenAwakens : MinecraftVersionRange(
        Release.V_1_21_4
    )

    object SpringToLife : MinecraftVersionRange(
        Release.V_1_21_5
    )

    object ChaseTheSkies : MinecraftVersionRange(
        Release.V_1_21_6,
        Release.V_1_21_8
    )

    @Incubating
    object Upcoming : MinecraftVersionRange(
        Release.V_1_21_9
    )
}
