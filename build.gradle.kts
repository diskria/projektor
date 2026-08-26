plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    `java-gradle-plugin`
    `maven-publish`
}

dependencies {
    implementation(gradleKotlinDsl())
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.html)

    implementation(libs.bundles.ktor.client)

    implementation(libs.bundles.implementation.settings.plugins)
    implementation(libs.bundles.implementation.project.plugins)
}

group = "io.github.diskria"
version = "8.0.5"

gradlePlugin {
    plugins {
        create("io.github.diskria.projektor") {
            id = "io.github.diskria.projektor"
            implementationClass = "io.github.diskria.projektor.ProjektorGradlePlugin"
        }
    }
}

val mavenName = "GithubPages"
val docsDirectory = layout.projectDirectory.dir("docs")

publishing {
    repositories {
        maven(docsDirectory) { name = mavenName }
    }
}

tasks {
    val cleanGithubPagesMaven = register<Delete>("clean${mavenName}Maven") {
        delete(docsDirectory)
    }
    val publish = "publishAllPublicationsTo${mavenName}Repository"
    named(publish) {
        mustRunAfter(cleanGithubPagesMaven)
    }
    register("releaseProjekt") {
        dependsOn(cleanGithubPagesMaven, publish)
    }
}
