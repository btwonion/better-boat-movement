package dev.nyon.bbm.extensions

import net.minecraft.resources.Identifier

fun identifier(location: String): Identifier? {
    return Identifier.tryParse(location)
}