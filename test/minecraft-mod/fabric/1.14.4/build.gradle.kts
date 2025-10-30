plugins {
    alias(libs.plugins.projektor)
}

projekt {
    minecraftMod {
        fabric {
            isApiRequired = true
        }
    }
}
