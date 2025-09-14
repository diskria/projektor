plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(gradleKotlinDsl())

    implementation(libs.build.config.plugin)
    implementation(libs.fabric.plugin)
    implementation(libs.neoforge.plugin)
    implementation(libs.modrinth.minotaur.plugin)

    implementation(libs.ktor.http)
    implementation(libs.kotlin.utils)
    implementation(libs.kotlin.serialization)

    constraints {
        // Override vulnerable transitive dependency (Okio < 3.4.0, CVE-2023-3635)
        // com.modrinth.minotaur → Modrinth4J → Okio
        implementation(libs.okio)
    }
}

sourceSets {
    main {
        kotlin {
            srcDirs("../src")
        }
    }
}
