package io.github.diskria.projektor.projekt.common

import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.projekt.*
import io.github.diskria.projektor.publishing.PublishingTarget
import org.gradle.api.Project

data class Projekt(
    override val owner: String,
    override val developer: String,
    override val email: String,
    override val repo: String,
    override val name: String,
    override val description: String,
    override val tags: Set<String>,
    override val version: String,
    override val license: License,
    override val publishingTarget: PublishingTarget?,
    override val javaVersion: Int,
    override val kotlinVersion: String,
) : IProjekt {

    fun toGradlePlugin(projectProvider: () -> Project): GradlePlugin =
        GradlePlugin(this, projectProvider)

    fun toKotlinLibrary(projectProvider: () -> Project): KotlinLibrary =
        KotlinLibrary(this, projectProvider)

    fun toAndroidLibrary(projectProvider: () -> Project): AndroidLibrary =
        AndroidLibrary(this, projectProvider)

    fun toAndroidApplication(projectProvider: () -> Project): AndroidApplication =
        AndroidApplication(this, projectProvider)

    fun toMinecraftMod(projectProvider: () -> Project): MinecraftMod =
        MinecraftMod(this, projectProvider)
}
