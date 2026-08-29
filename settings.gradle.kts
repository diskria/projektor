plugins {
    id("io.github.diskria.projektor") version "8.0.10"
}

projekt {
    version = "8.0.11"
    license { mit() }
    buildLogic {
        gradlePlugin(":envs-generator")
    }
    gradlePlugin()
}
