plugins {
    id("io.github.diskria.projektor") version "8.0.13"
}

projektor {
    version = "8.0.13"
    license { mit() }
    buildLogic {
        gradlePlugin(":metadata-generator")
    }
    gradlePlugin()
}
