package dev.nyon.bbm.paper.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.NamespacedKey

@Serializable(with = PaperIdentifierSerializer::class)
data class PaperIdentifier(val key: NamespacedKey, val isTag: Boolean) {
    override fun toString(): String = "${if (isTag) "#" else ""}$key"
}

object PaperIdentifierSerializer : KSerializer<PaperIdentifier> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("identifier", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): PaperIdentifier = decodeFromString(decoder.decodeString())

    fun decodeFromString(value: String): PaperIdentifier {
        val trimmed = value.trim()
        val isTag = trimmed.startsWith('#')
        val key = NamespacedKey.fromString(if (isTag) trimmed.drop(1) else trimmed)
            ?: throw IllegalArgumentException("Invalid Minecraft identifier: $value")
        return PaperIdentifier(key, isTag)
    }

    override fun serialize(encoder: Encoder, value: PaperIdentifier) = encoder.encodeString(value.toString())
}
