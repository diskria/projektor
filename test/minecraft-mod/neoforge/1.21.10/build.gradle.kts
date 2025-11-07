import io.github.diskria.projektor.minecraft.ModEnvironment

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.projektor)
}

projekt {
    minecraftMod {
        environment = ModEnvironment.CLIENT_SERVER
    }
}
