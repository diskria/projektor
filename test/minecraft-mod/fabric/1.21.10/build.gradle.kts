plugins {
    alias(libs.plugins.kotlin.jvm)
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
