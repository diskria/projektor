plugins {
    id("io.github.diskria.projektor") version "8.0.11"
}

projekt {
    version = "8.0.12"
    license { mit() }
    buildLogic {
        gradlePlugin(":envs-generator")
    }
    gradlePlugin()
}
