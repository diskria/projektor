package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.kotlin.utils.words.SpaceCase
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.owner.ProjektOwner
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

data class Projekt(
    override val owner: ProjektOwner,
    override val license: License,
    override val name: String,
    override val description: String,
    override val semver: Semver,
    override val slug: String,
    override val packageName: String,
    override val classNameBase: String = name.setCase(SpaceCase, PascalCase),
    override val javaVersion: Int,
    override val jvmTarget: JvmTarget,
    override val kotlinVersion: String,
    override val scm: ScmType = ScmType.GIT,
    override val softwareForge: SoftwareForgeType = owner.softwareForgeType,
    override val project: Project,
) : IProjekt {

    fun gradlePlugin(): GradlePlugin =
        GradlePlugin(this)

    fun kotlinLibrary(): KotlinLibrary =
        KotlinLibrary(this)

    fun androidLibrary(): AndroidLibrary =
        AndroidLibrary(this)

    fun androidApplication(): AndroidApplication =
        AndroidApplication(this)

    fun minecraftMod(): MinecraftMod =
        MinecraftMod(this)
}
