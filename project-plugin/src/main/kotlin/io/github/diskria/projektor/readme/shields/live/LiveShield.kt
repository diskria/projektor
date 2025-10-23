package io.github.diskria.projektor.readme.shields.live

import io.github.diskria.projektor.readme.shields.common.ReadmeShield

abstract class LiveShield : ReadmeShield() {
    abstract override fun getLabel(): String
    abstract override fun getUrl(): String
    abstract override fun getAlt(): String
}
