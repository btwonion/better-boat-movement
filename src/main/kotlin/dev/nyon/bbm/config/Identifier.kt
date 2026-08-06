package dev.nyon.bbm.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.resources.Identifier as MinecraftIdentifier

@Serializable(with = IdentifierSerializer::class)
data class Identifier(val original: MinecraftIdentifier, val isTag: Boolean) {
    override fun toString(): String = "${if (isTag) "#" else ""}$original"
}

object IdentifierSerializer : KSerializer<Identifier> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("identifier", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Identifier = decodeFromString(decoder.decodeString())

    fun decodeFromString(string: String): Identifier {
        val trimmed = string.trim()
        val isTag = trimmed.startsWith('#')
        return Identifier(MinecraftIdentifier.parse(if (isTag) trimmed.drop(1) else trimmed), isTag)
    }

    override fun serialize(encoder: Encoder, value: Identifier) = encoder.encodeString(value.toString())
}
