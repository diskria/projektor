import io.github.diskria.projektor.common.minecraft.sides.ModEnvironment

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.projektor)
}

projekt {
    minecraftMod {
        environment = ModEnvironment.CLIENT
    }
}
