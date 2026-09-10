plugins {
    id("io.github.diskria.projektor") version "8.0.15"
}

projektor {
    version = "8.0.15"
    license { mit() }
    buildLogic {
        gradlePlugin(":metadata-generator")
    }
    gradlePlugin()
}
