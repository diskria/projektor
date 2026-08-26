package io.github.diskria.projektor.core.model.metadata

import java.io.File

internal data class ProjektAbout(
    val description: String,
    val details: String,
    val tags: Set<String>
) {
    private val wordCaseDictionary: Map<String, String> by lazy {
        Regex("[A-Za-z0-9]+")
            .findAll("$description\n$details")
            .map { it.value }
            .groupBy { it.lowercase() }
            .mapValues { (_, variants) -> variants.maxBy { rateCase(it) } }
    }

    fun fixCase(word: String): String = wordCaseDictionary[word.lowercase()] ?: word

    private fun rateCase(word: String): Int {
        var score = 0
        if (word.any { it.isUpperCase() }) score += 1
        if (word.drop(1).any { it.isUpperCase() }) score += 2
        return score
    }

    companion object {
        fun of(repoDirectory: File): ProjektAbout {
            val aboutDirectory = repoDirectory.resolve("about").apply { mkdirs() }
            val descriptionFile = aboutDirectory.resolve("DESCRIPTION.md").getOrCreate(
                defaultContent = "TODO: Project description."
            )
            val detailsFile = aboutDirectory.resolve("DETAILS.md").getOrCreate(
                defaultContent = "TODO: Detailed project documentation."
            )
            val tagsFile = aboutDirectory.resolve("TAGS.md").getOrCreate(
                defaultContent = "kotlin"
            )
            return ProjektAbout(
                description = descriptionFile.readText().trim(),
                details = detailsFile.readText().trim(),
                tags = tagsFile.readLines().map { it.trim() }.filter { it.isNotEmpty() }.toSet()
            )
        }

        private fun File.getOrCreate(defaultContent: String): File {
            if (!exists()) {
                createNewFile()
                writeText(defaultContent)
            }
            return this
        }
    }
}
