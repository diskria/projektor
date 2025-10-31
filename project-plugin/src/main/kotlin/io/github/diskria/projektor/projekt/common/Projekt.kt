package io.github.diskria.projektor.projekt.common

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.`dot․case`
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.common.`path∕case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.common.repo.github.GithubRepo
import io.github.diskria.projektor.extensions.mappers.toJvmTarget
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.publishing.common.PublishingTarget
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

interface Projekt {

    val metadata: ProjektMetadata
    val type: ProjektType
    val repo: GithubRepo
    val packageNameBase: String
    val name: String
    val version: String
    val description: String
    val tags: Set<String>
    val license: License
    val publishingTargets: List<PublishingTarget>

    val isJavadocEnabled: Boolean get() = true
    val publicationComponentName: String? get() = null
    val packageNameSuffix: String? get() = null
    val jvmTarget: JvmTarget get() = Versions.JAVA.toJvmTarget()
    val archiveVersion: String get() = version
    val packageName: String get() = packageNameSuffix?.let { packageNameBase.appendPackageName(it) } ?: packageNameBase
    val packagePath: String get() = packageName.setCase(`dot․case`, `path∕case`)
    val classNamePrefix: String get() = repo.name.setCase(`kebab-case`, PascalCase)

    fun getBuildConfigFields(): List<Property<String>> = emptyList()
}
