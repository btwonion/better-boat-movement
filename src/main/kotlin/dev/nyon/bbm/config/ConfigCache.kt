package dev.nyon.bbm.config

import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

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
    return blockIdentifiers.mapTo(mutableSetOf()) { registry.get(it).get().value() }
}

private fun ResourceLocation.getTagEntries(): List<ResourceLocation> {
    val tagKey = TagKey.create(Registries.BLOCK, this)
    val entries = registry.getTagOrEmpty(tagKey)
    return entries.map { it.unwrapKey().get().location() }
}