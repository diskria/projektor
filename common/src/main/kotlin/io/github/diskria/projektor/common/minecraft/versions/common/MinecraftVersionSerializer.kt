package io.github.diskria.projektor.common.minecraft.versions.common

import io.github.diskria.kotlin.utils.extensions.common.KotlinSerializer
import io.github.diskria.kotlin.utils.extensions.common.className
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MinecraftVersionSerializer : KotlinSerializer<MinecraftVersion> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        MinecraftVersion::class.className(), PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: MinecraftVersion) {
        encoder.encodeString(value.asString())
    }

    override fun deserialize(decoder: Decoder): MinecraftVersion =
        MinecraftVersion.parse(decoder.decodeString())
}
