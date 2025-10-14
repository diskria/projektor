package io.github.diskria.projektor.common.projekt

import io.github.diskria.projektor.common.licenses.License

data class ProjektMetadata(
    val owner: String,
    val developer: String,
    val email: String,
    val repo: String,
    val name: String,
    val description: String,
    val version: String,
    val license: License,
    val tags: Set<String>,
)
