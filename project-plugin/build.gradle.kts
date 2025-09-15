import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//import io.github.diskria.projektor.gradle.extensions.configureGradlePlugin
//import io.github.diskria.projektor.gradle.extensions.configureProjekt
//import io.github.diskria.projektor.gradle.extensions.configurePublishing
//import io.github.diskria.projektor.gradle.extensions.gradlePlugin
//import io.github.diskria.projektor.gradle.extensions.projekt
//import io.github.diskria.projektor.gradle.extensions.requirePlugins
//import io.github.diskria.projektor.licenses.License
//import io.github.diskria.projektor.licenses.MitLicense
//import io.github.diskria.projektor.owner.GithubOwner
//import io.github.diskria.projektor.owner.GithubProfile
//import io.github.diskria.projektor.projekt.GradlePlugin
//import io.github.diskria.projektor.projekt.PublishingTarget
//import io.github.diskria.utils.kotlin.Constants

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    compileOnly(kotlin("gradle-plugin"))
    compileOnly(gradleKotlinDsl())

    compileOnly(libs.build.config.plugin)
    compileOnly(libs.fabric.plugin)
    compileOnly(libs.neoforge.plugin)
    compileOnly(libs.modrinth.minotaur.plugin)

    implementation(libs.ktor.http)
    implementation(libs.kotlin.utils)
    implementation(libs.kotlin.serialization)

    constraints {
        // Override vulnerable transitive dependency (Okio < 3.4.0, CVE-2023-3635)
        // com.modrinth.minotaur → Modrinth4J → Okio
        implementation(libs.okio)
    }
}

group = "io.github.diskria"
version = "1.2.0"

base {
    archivesName = "projektor"
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.ADOPTIUM)
        implementation.set(JvmImplementation.VENDOR_SPECIFIC)
    }
    withSourcesJar()
    withJavadocJar()
}
kotlin {
    jvmToolchain(21)
}
tasks.withType<JavaCompile>().configureEach {
    with(options) {
        release.set(21)
        encoding = Charsets.UTF_8.toString()
    }
}
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}
tasks.named<org.gradle.jvm.tasks.Jar>("jar") {
    from("LICENSE") {
        rename { oldName ->
            oldName + "_" + "projektor"
        }
    }
    archiveVersion.set("1.1.2")
}
sourceSets {
    named("main") {
        val generatedDirectory = "src/main/generated"

        resources.srcDirs(generatedDirectory)
        java.srcDirs("$generatedDirectory/java")
    }
}

gradlePlugin {
    plugins {
        create("projectPlugin") {
            id = "io.github.diskria.projektor"
            implementationClass = "io.github.diskria.projektor.ProjectGradlePlugin"
            displayName = "Projektor"
            description = "Gradle plugin with reusable conventions and helpers for projects from my GitHub organizations."
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven(layout.buildDirectory.dir("repo")) {
            name = "GithubPages"
        }
    }
}
