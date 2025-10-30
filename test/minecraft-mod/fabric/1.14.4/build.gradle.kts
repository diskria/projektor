plugins {
    alias(libs.plugins.projektor)
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.ornithe.ploceus)
}

projekt {
    minecraftMod {
        fabric()
    }
}
