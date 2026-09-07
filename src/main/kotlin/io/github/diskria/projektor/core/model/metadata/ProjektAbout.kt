package io.github.diskria.projektor.core.model.metadata

import io.github.diskria.projektor.extensions.writeTextCreatingParent
import org.gradle.api.file.Directory
import java.io.Serializable as PropertySerializable

class ProjektAbout(
    val description: String,
    val details: String,
    val tags: Set<String>,
) : PropertySerializable {

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
        fun from(repoDirectory: Directory): ProjektAbout {
            val aboutDirectory = repoDirectory.dir("about")
            val descriptionFile = aboutDirectory.file("DESCRIPTION.md")
            if (!descriptionFile.asFile.exists()) {
                descriptionFile.writeTextCreatingParent("TODO: Project description.")
            }
            val description = descriptionFile.asFile.readText().trim()

            val detailsFile = aboutDirectory.file("DETAILS.md")
            if (!detailsFile.asFile.exists()) {
                detailsFile.writeTextCreatingParent("TODO: Detailed project documentation.")
            }
            val details = descriptionFile.asFile.readText().trim()

            val tagsFile = aboutDirectory.file("TAGS.md")
            if (!tagsFile.asFile.exists()) {
                tagsFile.writeTextCreatingParent("kotlin")
            }
            val tags = tagsFile.asFile.readLines().map { it.trim() }.filter { it.isNotEmpty() }.toSet()
            return ProjektAbout(description, details, tags)
        }
    }
}
