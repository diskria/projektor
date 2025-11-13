import io.github.diskria.projektor.common.minecraft.era.Release
import io.github.diskria.projektor.common.minecraft.sides.ModEnvironment

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.projektor)
}

projekt {
    minecraftMod {
        environment = ModEnvironment.CLIENT_SERVER
        maxSupportedVersion = Release.V_1_20_4
    }
}
