package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.extensions.common.gradleError
import io.github.diskria.projektor.utils.VersionCatalogUtils
import io.github.diskria.utils.kotlin.BracketsType
import io.github.diskria.utils.kotlin.Constants
import io.github.diskria.utils.kotlin.extensions.asFile
import io.github.diskria.utils.kotlin.extensions.common.KotlinClass
import io.github.diskria.utils.kotlin.extensions.common.failWithInvalidValue
import io.github.diskria.utils.kotlin.extensions.common.failWithUnsupportedType
import io.github.diskria.utils.kotlin.extensions.common.fileName
import io.github.diskria.utils.kotlin.extensions.generics.joinBySpace
import io.github.diskria.utils.kotlin.extensions.parseOrNull
import io.github.diskria.utils.kotlin.extensions.wrap
import io.github.diskria.utils.kotlin.extensions.wrapWithBrackets
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import java.io.File
import java.util.*
import kotlin.jvm.optionals.getOrNull

inline fun <reified T> Provider<*>.value(): T =
    get().toString().parseOrNull<T>() as T

inline fun <reified T> Project.getProperty(propertiesFile: File, key: String): T =
    getPropertyOrNull(propertiesFile, key) ?: failWithInvalidValue(key)

inline fun <reified T> Project.getPropertyOrNull(propertiesFile: File, key: String): T? =
    Properties().apply {
        propertiesFile.inputStream().use { load(it) }
    }.getProperty(key, null)?.parseOrNull<T>()

inline fun <reified T : Any> Project.getExtensionOrThrow(): T =
    extensions.findByType(T::class.java) ?: failWithUnsupportedType(T::class)

fun <T : Task> Project.registerTasks(sealedClass: KotlinClass<T>) {
    sealedClass.sealedSubclasses.forEach { taskClass ->
        tasks.register(taskClass.getDisplayName(), taskClass.java)
    }
}

fun <T : Task> KotlinClass<T>.getDisplayName(): String =
    simpleName
        ?.removeSuffix("Task")
        ?.replaceFirstChar { it.lowercaseChar() }
        ?: failWithInvalidValue(simpleName)

fun Project.getLocalProperty(name: String): String? =
    getPropertyOrNull(rootDir.resolve(fileName("local", Constants.File.Extension.PROPERTIES)).asFile(), name)

fun Project.getBuildDirectory(): File =
    layout.buildDirectory.asFile.get()

fun Project.getDirectory(path: String): Directory =
    layout.projectDirectory.dir(path)

fun Project.getFile(path: String): RegularFile =
    layout.projectDirectory.file(path)

fun Project.rootDirectory(path: String): Directory =
    rootProject.getDirectory(path)

fun Project.rootFile(path: String): RegularFile =
    rootProject.getFile(path)

fun Project.buildDirectory(path: String): Provider<Directory> =
    layout.buildDirectory.dir(path)

fun Project.buildFile(path: String): Provider<RegularFile> =
    layout.buildDirectory.file(path)

fun Project.requirePlugins(vararg ids: String) {
    val unknownPluginIds = ids.filterNot { id -> pluginManager.hasPlugin(id) }
    if (unknownPluginIds.isNotEmpty()) {
        gradleError("Plugins required but not applied: ${unknownPluginIds.joinBySpace()}")
    }
}

fun Project.versionCatalogs(): VersionCatalogsExtension =
    extensions.findByType(VersionCatalogsExtension::class.java)
        ?: gradleError("Gradle version catalogs not supported")

fun Project.getCatalogVersion(alias: String, catalog: String = VersionCatalogUtils.DEFAULT_CATALOG_NAME): String? =
    versionCatalogs().named(catalog).findVersion(alias).getOrNull()?.requiredVersion

fun Project.getCatalogVersionOrThrow(
    alias: String,
    catalog: String = VersionCatalogUtils.DEFAULT_CATALOG_NAME,
): String =
    getCatalogVersion(alias, catalog) ?: gradleError(
        "Missing alias ${alias.wrap(Constants.Char.SINGLE_QUOTE)} in " +
                "${VersionCatalogUtils.CATALOG_VERSIONS.wrapWithBrackets(BracketsType.SQUARE)} of " +
                VersionCatalogUtils.buildCatalogFileName(catalog)
    )

fun Project.resolveCatalogVersion(
    aliasShort: String,
    catalog: String = VersionCatalogUtils.DEFAULT_CATALOG_NAME,
    formatShort: (String) -> String,
): String =
    getCatalogVersion("$aliasShort-full", catalog) ?: formatShort(getCatalogVersionOrThrow(aliasShort, catalog))

fun Project.getFileNames(directoryPath: String): List<String> =
    getDirectory(directoryPath)
        .asFile
        .listFiles { it.isFile && !it.isHidden }
        ?.map { it.nameWithoutExtension }
        .orEmpty()
