package io.github.diskria.projektor.common.projekt.metadata

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.ensureDirectoryExists
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import java.io.File

data class AboutMetadata(val description: String, val details: String, val tags: Set<String>) {

    companion object {
        private const val ABOUT_DIRECTORY_NAME: String = "about"
        private val DESCRIPTION_FILE_NAME: String = fileName("DESCRIPTION", Constants.File.Extension.MARKDOWN)
        private val DETAILS_FILE_NAME: String = fileName("DETAILS", Constants.File.Extension.MARKDOWN)
        private val TAGS_FILE_NAME: String = fileName("TAGS", Constants.File.Extension.MARKDOWN)

        fun of(projektDirectory: File): AboutMetadata {
            val aboutDirectory = projektDirectory.resolve(ABOUT_DIRECTORY_NAME).ensureDirectoryExists()
            val descriptionFile = aboutDirectory.resolve(DESCRIPTION_FILE_NAME).ensureFileExists()
            val detailsFile = aboutDirectory.resolve(DETAILS_FILE_NAME).ensureFileExists()
            val tagsFile = aboutDirectory.resolve(TAGS_FILE_NAME).ensureFileExists()
            return AboutMetadata(
                description = descriptionFile.readText().trim().ifEmpty {
                    missingMetadataError(projektDirectory, descriptionFile) {
                        "a short one-paragraph description of your project"
                    }
                },
                details = detailsFile.readText().trim().ifEmpty {
                    missingMetadataError(projektDirectory, detailsFile) {
                        "a detailed explanation of your project’s purpose, features, and usage"
                    }
                },
                tags = tagsFile.readLines().filter { it.isNotBlank() }.toSet().ifEmpty {
                    missingMetadataError(projektDirectory, tagsFile) {
                        "a few relevant tags, one per line"
                    }
                },
            )
        }

        private fun missingMetadataError(projektDirectory: File, targetFile: File, getContent: () -> String): Nothing =
            gradleError(
                buildString {
                    appendLine("Missing or empty projekt metadata file.")
                    appendLine("Path: ${targetFile.relativeTo(projektDirectory).path}")
                    appendLine("Please provide ${getContent()} in this file.")
                }
            )
    }
}
