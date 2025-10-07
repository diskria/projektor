package io.github.diskria.projektor.settings.projekt.common

import io.github.diskria.projektor.settings.licenses.License
import io.github.diskria.projektor.settings.projekt.*
import org.gradle.api.initialization.Settings

open class Projekt(
    override val owner: String,
    override val developer: String,
    override val repo: String,
    override val name: String,
    override val description: String,
    override val version: String,
    override val license: License,
    override val tags: Set<String>,
) : IProjekt {

    fun toGradlePlugin(settingsProvider: () -> Settings): GradlePlugin =
        GradlePlugin(this, settingsProvider)

    fun toKotlinLibrary(settingsProvider: () -> Settings): KotlinLibrary =
        KotlinLibrary(this, settingsProvider)

    fun toAndroidLibrary(settingsProvider: () -> Settings): AndroidLibrary =
        AndroidLibrary(this, settingsProvider)

    fun toAndroidApplication(settingsProvider: () -> Settings): AndroidApplication =
        AndroidApplication(this, settingsProvider)

    fun toMinecraftMod(settingsProvider: () -> Settings): MinecraftMod =
        MinecraftMod(this, settingsProvider)
}
