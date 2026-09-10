plugins {
    id("io.github.diskria.projektor") version "8.0.12"
}

projekt {
    version = "8.0.13"
    license { mit() }
    buildLogic {
        gradlePlugin(":metadata-generator")
    }
    gradlePlugin()
}
