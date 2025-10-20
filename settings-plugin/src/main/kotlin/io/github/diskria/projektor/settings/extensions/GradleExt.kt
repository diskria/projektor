package io.github.diskria.projektor.settings.extensions

import org.gradle.api.invocation.Gradle
import java.io.File

fun Gradle.findRootDirectoryFromCompositeBuildOrNull(): File? =
    generateSequence(this) { it.parent }.lastOrNull()?.startParameter?.currentDir
