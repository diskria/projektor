plugins {
    alias(libs.plugins.projektor) version "4.+"
    alias(libs.plugins.fabric.loom) apply false
}

projekt {
    minecraftMod {
        fabric {
            yarnBuild = 2
        }
    }
}
