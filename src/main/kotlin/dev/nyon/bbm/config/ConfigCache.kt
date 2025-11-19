package dev.nyon.bbm.config

import net.minecraft.core.HolderSet
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import kotlin.jvm.optionals.getOrDefault

var allowedSupportingBlocks: Set<Block> = setOf()
var allowedCollidingBlocks: Set<Block> = setOf()

private val registryAccess by lazy { RegistryAccess.ImmutableRegistryAccess(listOf(BuiltInRegistries.BLOCK)) }
private val registry by lazy { registryAccess.lookupOrThrow(Registries.BLOCK) }
internal fun loadBlocks(identifiers: MutableSet<Identifier>): Set<Block> {
    val blockIdentifiers: MutableSet<ResourceLocation> = mutableSetOf()
    identifiers.forEach { (original, isTag) ->
        if (!isTag) blockIdentifiers.add(original)
        else blockIdentifiers.addAll(original.getTagEntries())
    }
    return blockIdentifiers.mapTo(mutableSetOf()) {
        val location = /*? if >1.21 {*/ it /*?} else {*/ /*ResourceKey.create(Registries.BLOCK, it) *//*?}*/
        registry.get(location).get().value()
    }
}

private fun ResourceLocation.getTagEntries(): List<ResourceLocation> {
    val tagKey = TagKey.create(Registries.BLOCK, this)
    //? if <=1.21 {
    /*val entries = registry.get(tagKey)
    val locations = entries.getOrDefault(HolderSet.empty()).map { it.unwrapKey().get().location() }
    *///?} else >1.21 {
    val entries = registry.getTagOrEmpty(tagKey)
    val locations = entries.map { it.unwrapKey().get().location() }
    //?}

    return locations
}

internal fun reloadCache() {
    allowedSupportingBlocks = loadBlocks(config.boosting.allowedSupportingBlocks)
    allowedCollidingBlocks = loadBlocks(config.boosting.allowedCollidingBlocks)
}