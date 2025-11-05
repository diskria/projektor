package io.github.diskria.projektor.readme.shields.live

import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.ktor.http.*

abstract class LiveShield : ReadmeShield() {
    abstract override fun getLabel(): String
    abstract override fun getUrl(): Url
    abstract override fun getAlt(): String
}
