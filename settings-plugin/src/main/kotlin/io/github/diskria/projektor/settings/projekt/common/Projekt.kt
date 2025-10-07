package io.github.diskria.projektor.settings.projekt.common

import io.github.diskria.projektor.settings.licenses.License
import io.github.diskria.projektor.settings.projekt.*
import org.gradle.api.initialization.Settings

data class Projekt(
    override val owner: String,
    override val developer: String,
    override val repo: String,
    override val name: String,
    override val description: String,
    override val version: String,
    override val license: License,
    override val tags: Set<String>,
) : IProjekt {

    fun toGradlePlugin(settings: Settings): GradlePlugin =
        GradlePlugin(this, settings)

    fun toKotlinLibrary(settings: Settings): KotlinLibrary =
        KotlinLibrary(this, settings)

    fun toAndroidLibrary(settings: Settings): AndroidLibrary =
        AndroidLibrary(this, settings)

    fun toAndroidApplication(settings: Settings): AndroidApplication =
        AndroidApplication(this, settings)

    fun toMinecraftMod(settings: Settings): MinecraftMod =
        MinecraftMod(this, settings)
}