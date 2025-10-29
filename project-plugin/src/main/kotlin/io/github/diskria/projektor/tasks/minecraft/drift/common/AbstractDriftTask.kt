package io.github.diskria.projektor.tasks.minecraft.drift.common

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.getDirectory
import io.github.diskria.kotlin.utils.extensions.ensureDirectoryExists
import io.github.diskria.kotlin.utils.extensions.listDirectories
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.common.asString
import io.github.diskria.projektor.common.minecraft.versions.common.previousOrNull
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.generate.GenerateProjektGitIgnoreTask
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

abstract class AbstractDriftTask : DefaultTask() {

    @get:Internal
    abstract val loaderDirectory: DirectoryProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        loaderDirectory.convention(project.getDirectory(getLoader().getName()))
    }

    @TaskAction
    fun drift() {
        val loaderDirectory = loaderDirectory.get().asFile
        val versionDirectories = loaderDirectory.listDirectories()
        val minSupportedVersion = versionDirectories
            .map { MinecraftVersion.parse(it.name) }
            .minWith(MinecraftVersion.COMPARATOR)
        val versionToTest = minSupportedVersion.previousOrNull()?.asString() ?: gradleError("No version to test")

        val sourceDirectory = loaderDirectory.resolve(minSupportedVersion.asString())
        val targetDirectory = loaderDirectory.resolve(versionToTest).ensureDirectoryExists()
        val blackListDirectoryNames = listOf(
            GenerateProjektGitIgnoreTask.DOT_GRADLE_DIRECTORY_NAME,
            GenerateProjektGitIgnoreTask.BUILD_DIRECTORY_NAME,
            MinecraftMod.RUN_DIRECTORY_NAME,
        )
        Files.walk(sourceDirectory.toPath()).forEach { path ->
            val relative = sourceDirectory.toPath().relativize(path)
            val parts = relative.toString().split(File.separatorChar)
            if (parts.any { it in blackListDirectoryNames }) {
                return@forEach
            }
            val targetPath = targetDirectory.toPath().resolve(relative)
            if (Files.isDirectory(path)) {
                Files.createDirectories(targetPath)
            } else {
                Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    @Internal
    abstract fun getLoader(): ModLoader
}