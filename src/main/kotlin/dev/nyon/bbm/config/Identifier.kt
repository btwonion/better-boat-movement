package dev.nyon.bbm.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.resources.Identifier as MinecraftIdentifier

/** Preserves invalid user input so it can be warned about and skipped without aborting config loading. */
@Serializable(with = IdentifierSerializer::class)
data class Identifier(val value: String) {
    val isTag: Boolean get() = value.startsWith('#')
    val original: MinecraftIdentifier?
        get() = MinecraftIdentifier.tryParse(if (isTag) value.drop(1) else value)

    constructor(original: MinecraftIdentifier, isTag: Boolean) : this("${if (isTag) "#" else ""}$original")

    override fun toString(): String = value
}

object IdentifierSerializer : KSerializer<Identifier> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("identifier", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Identifier = decodeFromString(decoder.decodeString())

    fun decodeFromString(string: String): Identifier = Identifier(string.trim())

    override fun serialize(encoder: Encoder, value: Identifier) = encoder.encodeString(value.toString())
}
