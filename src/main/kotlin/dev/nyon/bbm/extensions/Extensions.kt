package dev.nyon.bbm.extensions

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.resources.ResourceKey

//? if >1.21.8
private val KEY_BINDING_CATEGORY: KeyMapping.Category = KeyMapping.Category(resourceLocation("bbm:main")!!)

fun keyMapping(location: String, key: Int): KeyMapping {
    return KeyMapping(location, InputConstants.Type.KEYSYM, key, /*? if >1.21.8 {*/ KEY_BINDING_CATEGORY/*?} else {*/  /*"key.category.bbm.main"*//*?}*/)
}

fun ResourceKey<*>.id(): ResourceLocation {
    return /*? if >=1.21.11 {*/identifier()/*?} else {*//*location()*//*?}*/
}