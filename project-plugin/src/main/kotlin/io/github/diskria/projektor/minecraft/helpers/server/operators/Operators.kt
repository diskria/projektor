package io.github.diskria.projektor.minecraft.helpers.server.operators

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Operators(val entries: List<OperatorEntry>)
