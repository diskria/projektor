package io.github.diskria.projektor.projekt

import io.github.diskria.gradle.utils.extensions.kotlin.common.gradleError
import io.github.diskria.gradle.utils.extensions.kotlin.semver
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.words.DotCase
import io.github.diskria.kotlin.utils.words.KebabCase
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.kotlin.utils.words.SpaceCase
import io.github.diskria.projektor.extensions.kotlin.mappers.toJvmTarget
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
) : IProjekt {

    fun toGradlePlugin(project: Project): GradlePlugin = GradlePlugin(this, project)

    fun toKotlinLibrary(project: Project): KotlinLibrary = KotlinLibrary(this, project)

    fun toAndroidLibrary(project: Project): AndroidLibrary = AndroidLibrary(this, project)

    fun toAndroidApplication(project: Project): AndroidApplication = AndroidApplication(this, project)

    fun toMinecraftMod(project: Project): MinecraftMod = MinecraftMod(this, project)

    companion object {
        fun of(project: Project, owner: ProjektOwner, license: License, jvmTarget: JvmTarget? = null): Projekt {
            val projectName = project.rootProject.name
            val javaVersion = Versions.JAVA
            return Projekt(
                owner = owner,
                license = license,
                name = projectName,
                description = project.rootProject.description ?: gradleError("Projekt description not set!"),
                semver = project.rootProject.semver(),
                slug = projectName.setCase(SpaceCase, KebabCase).lowercase(),
                packageName = owner.namespace + Constants.Char.DOT + projectName.setCase(SpaceCase, DotCase),
                javaVersion = javaVersion,
                jvmTarget = jvmTarget ?: javaVersion.toJvmTarget(),
                kotlinVersion = Versions.KOTLIN,
            )
        }
    }
}
