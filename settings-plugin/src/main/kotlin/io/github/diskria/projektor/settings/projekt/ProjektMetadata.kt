package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.licenses.License

data class ProjektMetadata(
    val owner: String,
    val developer: String,
    val repo: String,
    val name: String,
    val description: String,
    val version: String,
    val license: License,
    val tags: Set<String>,
)
