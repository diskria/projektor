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

    fun toGradlePlugin(project: Project): GradlePlugin =
        GradlePlugin(this, project)

    fun toKotlinLibrary(project: Project): KotlinLibrary =
        KotlinLibrary(this, project)

    fun toAndroidLibrary(project: Project): AndroidLibrary =
        AndroidLibrary(this, project)

    fun toAndroidApplication(project: Project): AndroidApplication =
        AndroidApplication(this, project)

    fun toMinecraftMod(project: Project): MinecraftMod =
        MinecraftMod(this, project)
}
