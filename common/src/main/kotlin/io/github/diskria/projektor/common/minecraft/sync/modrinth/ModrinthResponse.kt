package io.github.diskria.projektor.common.minecraft.sync.modrinth

import io.github.diskria.kotlin.utils.serialization.annotations.IgnoreUnknownKeys
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
@IgnoreUnknownKeys
value class ModrinthResponse(val versions: List<ModrinthVersion>)
