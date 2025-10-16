package io.github.diskria.projektor.extensions.mappers

import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.common.projekt.ProjektMetadata
import io.github.diskria.projektor.projekt.common.Projekt

fun ProjektMetadata.mapToProjekt(): Projekt =
    Projekt(
        type = type,
        owner = owner,
        developer = developer,
        email = email,
        repo = repo,
        name = name,
        description = description,
        version = version,
        tags = tags,
        license = license.mapToModel(),
        publishingTarget = publishingTarget.mapToModel(),
        javaVersion = Versions.JAVA,
        kotlinVersion = Versions.KOTLIN,
    )
