package io.github.diskria.projektor.extensions

import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.assign

fun AbstractArchiveTask.copyArchiveNameParts(
    source: AbstractArchiveTask,
    classifier: String = source.archiveClassifier.get(),
) {
    archiveBaseName = source.archiveBaseName
    archiveAppendix = source.archiveAppendix
    archiveVersion = source.archiveVersion
    archiveClassifier = classifier
    archiveExtension = source.archiveExtension
}
