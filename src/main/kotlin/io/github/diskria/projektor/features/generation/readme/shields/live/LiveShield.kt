package io.github.diskria.projektor.features.generation.readme.shields.live

import io.github.diskria.projektor.features.generation.readme.shields.common.ReadmeShield

internal abstract class LiveShield : ReadmeShield() {
    abstract override fun getLabel(): String
    abstract override fun getUrl(): String?
    abstract override fun getAlt(): String
}
