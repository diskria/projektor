package io.github.diskria.projektor.tasks.minecraft.drift.common

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.getDirectory
import io.github.diskria.kotlin.utils.extensions.ensureDirectoryExists
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.listDirectories
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.ProjectDirectories
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.common.asString
import io.github.diskria.projektor.common.minecraft.versions.common.previousOrNull
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class AbstractDriftTask : DefaultTask() {

    @get:Internal
    abstract val loaderDirectory: DirectoryProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        loaderDirectory.convention(project.getDirectory(getLoader().getName()))
    }

    @Internal
    abstract fun getLoader(): ModLoader

    @TaskAction
    fun drift() {
        val loaderDirectory = loaderDirectory.get().asFile
        val minSupportedVersion = loaderDirectory
            .listDirectories()
            .map { MinecraftVersion.parse(it.name) }
            .minWith(MinecraftVersion.COMPARATOR)
        val versionToTest = minSupportedVersion
            .previousOrNull()
            ?.asString()
            ?: gradleError("No version to test")

        val sourceDirectory = loaderDirectory.resolve(minSupportedVersion.asString())
        val targetDirectory = loaderDirectory.resolve(versionToTest).ensureDirectoryExists()
        val blackListDirectoryNames = listOf(
            ProjectDirectories.GRADLE_CACHE,
            ProjectDirectories.BUILD,
            MinecraftMod.RUN_DIRECTORY_NAME,
        )
        sourceDirectory.listFiles()?.filterNot { it.name in blackListDirectoryNames }?.forEach { rootFile ->
            val target = targetDirectory.resolve(rootFile.name)
            if (rootFile.isDirectory) {
                rootFile.copyRecursively(target)
            } else if (!target.exists()) {
                rootFile.copyTo(target.ensureFileExists(), overwrite = true)
            }
        }
    }
}
