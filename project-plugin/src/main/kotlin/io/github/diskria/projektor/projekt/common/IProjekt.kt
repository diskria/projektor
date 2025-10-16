package io.github.diskria.projektor.projekt.common

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.`dot․case`
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.common.`path∕case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.mappers.toJvmTarget
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.publishing.common.PublishingTarget
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

interface IProjekt {

    val metadata: ProjektMetadata
    val license: License
    val publishingTarget: PublishingTarget
    val javaVersion: Int
    val kotlinVersion: String

    val packageName: String
        get() = metadata.repository.owner.namespace.appendPackageName(
            metadata.repository.name.setCase(`kebab-case`, `dot․case`)
        )

    val jvmTarget: JvmTarget
        get() = javaVersion.toJvmTarget()

    val archiveVersion: String
        get() = metadata.version

    val packagePath: String
        get() = packageName.setCase(`dot․case`, `path∕case`)

    val classNamePrefix: String
        get() = metadata.repository.name.setCase(`kebab-case`, PascalCase)

    fun getBuildConfigFields(): List<Property<String>> = emptyList()
}
