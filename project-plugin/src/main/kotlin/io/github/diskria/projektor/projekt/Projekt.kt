package io.github.diskria.projektor.projekt

import io.github.diskria.gradle.utils.extensions.kotlin.common.gradleError
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.words.*
import io.github.diskria.projektor.extensions.kotlin.mappers.toJvmTarget
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.minecraft.ModLoader
import io.github.diskria.projektor.minecraft.version.MinecraftVersion
import io.github.diskria.projektor.owner.ProjektOwner
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

data class Projekt(
    override var owner: ProjektOwner,
    override var license: License,
    override var name: String,
    override var description: String,
    override var semver: Semver,
    override var slug: String,
    override var packageName: String,
    override var packagePath: String = packageName.setCase(DotCase, PathCase),
    override var classNameBase: String = name.setCase(SpaceCase, PascalCase),
    override var javaVersion: Int,
    override var jvmTarget: JvmTarget,
    override var kotlinVersion: String,
    override var scm: ScmType = ScmType.GIT,
    override var softwareForge: SoftwareForgeType = owner.softwareForgeType,
) : IProjekt {

    fun toGradlePlugin(): GradlePlugin = GradlePlugin(this)

    fun toKotlinLibrary(): KotlinLibrary = KotlinLibrary(this)

    fun toMinecraftMod(
        modLoader: ModLoader,
        minecraftVersion: MinecraftVersion
    ): MinecraftMod =
        MinecraftMod(this, modLoader, minecraftVersion)

    fun toAndroidApplication(): AndroidApplication = AndroidApplication(this)

    companion object {
        fun of(project: Project, owner: ProjektOwner, license: License, jvmTarget: JvmTarget? = null): Projekt {
            val projectName = project.rootProject.name
            val versionString = project.rootProject.version as? String ?: gradleError("Project version must be String!")
            val semver = Semver.of(versionString)
            val javaVersion = Versions.JAVA
            return Projekt(
                owner = owner,
                license = license,
                name = projectName,
                description = project.rootProject.description ?: gradleError("Projekt description not set!"),
                semver = semver,
                slug = projectName.setCase(SpaceCase, KebabCase).lowercase(),
                packageName = owner.namespace + Constants.Char.DOT + projectName.setCase(SpaceCase, DotCase),
                javaVersion = javaVersion,
                jvmTarget = jvmTarget ?: javaVersion.toJvmTarget(),
                kotlinVersion = Versions.KOTLIN,
            )
        }
    }
}
