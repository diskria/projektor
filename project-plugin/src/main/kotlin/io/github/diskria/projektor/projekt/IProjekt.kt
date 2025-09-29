package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.owner.ProjektOwner
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

interface IProjekt {
    var owner: ProjektOwner
    var license: License
    var name: String
    var description: String
    var semver: Semver
    var slug: String
    var packageName: String
    var packagePath: String
    var classNameBase: String
    var javaVersion: Int
    var jvmTarget: JvmTarget
    var kotlinVersion: String
    var scm: ScmType
    var softwareForge: SoftwareForgeType
}
