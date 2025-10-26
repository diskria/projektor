plugins {
    `kotlin-dsl`
    alias(libs.plugins.projektor)
}

dependencies {
    compileOnly(libs.bundles.diskria.utils)
    compileOnly(libs.bundles.ktor.client)
}

projekt {
    kotlinLibrary()
}
