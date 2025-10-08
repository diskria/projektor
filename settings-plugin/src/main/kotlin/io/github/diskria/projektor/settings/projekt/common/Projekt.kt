package io.github.diskria.projektor.settings.projekt.common

import io.github.diskria.projektor.settings.licenses.License

data class Projekt(
    override val owner: String,
    override val repo: String,
    override val description: String,
    override val version: String,
    override val license: License,
    override val tags: Set<String>,
) : IProjekt
