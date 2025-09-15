import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//import io.github.diskria.projektor.gradle.extensions.configureGradlePlugin
//import io.github.diskria.projektor.owner.GithubProfile
//import io.github.diskria.projektor.projekt.PublishingTarget

plugins {
    `kotlin-dsl`
    `maven-publish`
}

dependencies {
    implementation(libs.kotlin.utils)
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
        create("settingsPlugin") {
            id = "io.github.diskria.projektor.settings"
            implementationClass = "io.github.diskria.projektor.settings.ProjektorSettingsGradlePlugin"
            displayName = "Projektor"
            description =
                "Gradle plugin with reusable conventions and helpers for projects from my GitHub organizations."
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
