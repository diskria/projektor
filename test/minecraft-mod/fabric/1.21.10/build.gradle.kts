plugins {
    alias(libs.plugins.projektor)
    alias(libs.plugins.fabric.loom)
}

projekt {
    minecraftMod {
        fabric {
            yarnBuild = 2
        }
    }
}
