package dev.nyon.bbm.extensions

fun resourceLocation(location: String): ResourceLocation? {
    return ResourceLocation.tryParse(location)
}