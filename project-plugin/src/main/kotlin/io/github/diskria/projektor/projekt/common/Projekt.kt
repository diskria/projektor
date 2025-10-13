package io.github.diskria.projektor.projekt.common

import io.github.diskria.projektor.common.licenses.License
import io.github.diskria.projektor.publishing.common.PublishingTarget

data class Projekt(
    override val owner: String,
    override val developer: String,
    override val email: String,
    override val repo: String,
    override val name: String,
    override val description: String,
    override val tags: Set<String>,
    override val version: String,
    override val license: License,
    override val publishingTarget: PublishingTarget?,
    override val javaVersion: Int,
    override val kotlinVersion: String,
) : IProjekt
