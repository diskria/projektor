package io.github.diskria.projektor.common.minecraft.sync.packs

import io.github.diskria.projektor.common.minecraft.sync.packs.common.AbstractPackFormatSynchronizer

object ResourcePackFormatSynchronizer : AbstractPackFormatSynchronizer() {
    override val componentName: String = "resource-pack-format"
    override val wikiTableCaption: String = "Resource pack formats"
}
