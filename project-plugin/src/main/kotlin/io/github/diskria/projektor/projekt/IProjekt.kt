package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.words.DotCase
import io.github.diskria.kotlin.utils.words.PathCase
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.owner.ProjektOwner
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

interface IProjekt {
    val owner: ProjektOwner
    val license: License
    val name: String
    val description: String
    val semver: Semver
    val slug: String
    val packageName: String
    val classNameBase: String
    val javaVersion: Int
    val jvmTarget: JvmTarget
    val kotlinVersion: String
    val scm: ScmType
    val softwareForge: SoftwareForgeType

    val packagePath: String
        get() = packageName.setCase(DotCase, PathCase)
}
