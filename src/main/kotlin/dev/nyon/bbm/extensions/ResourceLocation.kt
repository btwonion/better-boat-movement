package dev.nyon.bbm.extensions

import net.minecraft.resources.ResourceLocation

fun resourceLocation(location: String): ResourceLocation? {
    return ResourceLocation.tryParse(location)
}