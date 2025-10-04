package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.mappers.toEnum
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.toAutoNamedProperty
import io.github.diskria.projektor.extensions.kotlin.mappers.toJvmTarget
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.minecraft.ModLoader
import io.github.diskria.projektor.minecraft.utils.ModrinthUtils
import io.github.diskria.projektor.minecraft.version.MinecraftVersion
import io.github.diskria.projektor.minecraft.version.getMinJavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import kotlin.properties.Delegates

open class MinecraftMod(private val projekt: IProjekt, project: Project) : IProjekt by projekt {

    val id: String = projekt.repo
    val mixinsConfigFileName: String = fileName(id, "mixins", Constants.File.Extension.JSON)
    val minecraftVersion = MinecraftVersion.of(project.projectDir.name)
    val modLoader = project.projectDir.parentFile.name.toEnum<ModLoader>()

    val modrinthProjectUrl: String
        get() = ModrinthUtils.getProjectUrl(modrinthProjectId)

    var modrinthProjectId: String by Delegates.notNull()
    var environment: ModEnvironment by Delegates.notNull()
    var isFabricApiRequired: Boolean by Delegates.notNull()

    override val jvmTarget: JvmTarget = minecraftVersion.getMinJavaVersion().toJvmTarget()

    override fun getMetadata(): List<Property<String>> {
        val modId by id.toAutoNamedProperty(SCREAMING_SNAKE_CASE)
        val modName by name.toAutoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(modId, modName)
    }
}
