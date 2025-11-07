package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.common.ProjectDirectories
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.get

val SourceSetContainer.mixins: SourceSet
    get() = this[ProjectDirectories.MINECRAFT_MIXINS]
