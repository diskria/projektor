plugins {
    alias(convention.plugins.projektor)
}

dependencies {
    implementation(libs.snake.yaml)
    implementation(libs.poetesse)
}

projekt {
    gradlePlugin()
}
