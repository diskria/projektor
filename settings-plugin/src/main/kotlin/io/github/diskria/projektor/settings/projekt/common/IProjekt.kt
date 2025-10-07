package io.github.diskria.projektor.settings.projekt.common

import io.github.diskria.projektor.settings.licenses.License

interface IProjekt {
    val owner: String
    val developer: String
    val repo: String
    val name: String
    val description: String
    val version: String
    val license: License
    val tags: Set<String>
}
