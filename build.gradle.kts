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
version = "8.0.1"

gradlePlugin {
    plugins {
        create("io.github.diskria.projektor") {
            id = "io.github.diskria.projektor"
            implementationClass = "io.github.diskria.projektor.ProjektorGradlePlugin"
        }
    }
}

val mavenName = "GithubPages"
val githubPagesMavenDir = layout.projectDirectory.dir("docs")

publishing {
    repositories {
        mavenLocal()
        maven(githubPagesMavenDir) { name = mavenName }
    }
}

tasks {
    val cleanGithubPagesMaven = register<Delete>("clean${mavenName}Maven") {
        delete(githubPagesMavenDir)
    }

    named("publishAllPublicationsTo${mavenName}Repository") {
        mustRunAfter(cleanGithubPagesMaven)
    }

    register("releaseProjekt") {
        dependsOn(
            cleanGithubPagesMaven,
            "publishAllPublicationsTo${mavenName}Repository",
            "publishAllPublicationsToMavenLocalRepository",
        )
    }
}
