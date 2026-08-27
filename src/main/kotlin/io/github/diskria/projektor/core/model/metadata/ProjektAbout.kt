package io.github.diskria.projektor.core.model.metadata

import java.io.File
import java.io.Serializable

internal data class ProjektAbout(
    val description: String,
    val details: String,
    val tags: Set<String>,
) : Serializable {

    fun fixCase(word: String): String {
        val target = word.lowercase()
        return Regex("[A-Za-z0-9]+")
            .findAll("$description\n$details")
            .map { it.value }
            .filter { it.lowercase() == target }
            .maxByOrNull { rateCase(it) } ?: word
    }

    private fun rateCase(word: String): Int {
        var score = 0
        if (word.any { it.isUpperCase() }) score += 1
        if (word.drop(1).any { it.isUpperCase() }) score += 2
        return score
    }

    companion object {
        fun from(repoDirectory: File): ProjektAbout {
            val aboutDirectory = repoDirectory.resolve("about").apply { mkdirs() }

            val description = aboutDirectory.resolve("DESCRIPTION.md").ensureCreated("TODO: Project description.")
                .readText().trim()
            val details = aboutDirectory.resolve("DETAILS.md").ensureCreated("TODO: Detailed project documentation.")
                .readText().trim()
            val tags = aboutDirectory.resolve("TAGS.md").ensureCreated("kotlin")
                .readLines().map { it.trim() }.filter { it.isNotEmpty() }.toSet()
            return ProjektAbout(description, details, tags)
        }

        private fun File.ensureCreated(defaultContent: String): File {
            if (!exists()) {
                createNewFile()
                writeText(defaultContent)
            }
            return this
        }
    }
}
