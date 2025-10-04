package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.projektor.licenses.License
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

data class Projekt(
    override val owner: String,
    override val repo: String,
    override val description: String,
    override val semver: Semver,
    override val email: String,
    override val license: License,
    override val javaVersion: Int,
    override val jvmTarget: JvmTarget,
    override val kotlinVersion: String,
) : IProjekt {

    fun toGradlePlugin(): GradlePlugin =
        GradlePlugin(this)

    fun toKotlinLibrary(): KotlinLibrary =
        KotlinLibrary(this)

    fun toAndroidLibrary(): AndroidLibrary =
        AndroidLibrary(this)

    fun toAndroidApplication(): AndroidApplication =
        AndroidApplication(this)

    fun toMinecraftMod(project: Project): MinecraftMod =
        MinecraftMod(this, project)
}
