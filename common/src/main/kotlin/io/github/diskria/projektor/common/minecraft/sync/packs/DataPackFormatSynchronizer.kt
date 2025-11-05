package io.github.diskria.projektor.common.minecraft.sync.packs

import io.github.diskria.projektor.common.minecraft.sync.packs.common.AbstractPackFormatSynchronizer

object DataPackFormatSynchronizer : AbstractPackFormatSynchronizer() {
    override val componentName: String = "data-pack-format"
    override val wikiTableCaption: String = "Data pack formats"
}
