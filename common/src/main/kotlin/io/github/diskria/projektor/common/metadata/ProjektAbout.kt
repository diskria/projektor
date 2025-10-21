package io.github.diskria.projektor.common.metadata

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.ensureDirectoryExists
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import java.io.File

data class ProjektAbout(val description: String, val details: String, val tags: Set<String>) {

    companion object {
        private const val ABOUT_DIRECTORY_NAME: String = "about"

        private val DESCRIPTION_FILE_NAME: String = fileName("DESCRIPTION", Constants.File.Extension.MARKDOWN)
        private val DETAILS_FILE_NAME: String = fileName("DETAILS", Constants.File.Extension.MARKDOWN)
        private val TAGS_FILE_NAME: String = fileName("TAGS", Constants.File.Extension.MARKDOWN)

        fun of(repoDirectory: File): ProjektAbout {
            val aboutDirectory = repoDirectory.resolve(ABOUT_DIRECTORY_NAME).ensureDirectoryExists()
            val descriptionFile = aboutDirectory.resolve(DESCRIPTION_FILE_NAME).ensureFileExists()
            val detailsFile = aboutDirectory.resolve(DETAILS_FILE_NAME).ensureFileExists()
            val tagsFile = aboutDirectory.resolve(TAGS_FILE_NAME).ensureFileExists()
            return ProjektAbout(
                description = descriptionFile.readText().trim().ifEmpty {
                    missingMetadataError(repoDirectory, descriptionFile) {
                        "a short one-paragraph description of your project"
                    }
                },
                details = detailsFile.readText().trim().ifEmpty {
                    missingMetadataError(repoDirectory, detailsFile) {
                        "a detailed explanation of your project’s purpose, features, and usage"
                    }
                },
                tags = tagsFile.readLines().filter { it.isNotBlank() }.toSet().ifEmpty {
                    missingMetadataError(repoDirectory, tagsFile) {
                        "a few relevant tags, one per line"
                    }
                },
            )
        }

        private fun missingMetadataError(repoDirectory: File, targetFile: File, content: () -> String): Nothing =
            gradleError(
                buildString {
                    appendLine("Missing or empty projekt metadata file.")
                    appendLine("Path: ${targetFile.relativeTo(repoDirectory).path}")
                    appendLine("Please provide ${content()} in this file.")
                }
            )
    }
}
