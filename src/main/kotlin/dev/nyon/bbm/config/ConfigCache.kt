package dev.nyon.bbm.config

import net.minecraft.resources.Identifier as MinecraftIdentifier
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

var allowedSupportingBlocks: Set<Block> = setOf()
var allowedCollidingBlocks: Set<Block> = setOf()

private val registryAccess by lazy { RegistryAccess.ImmutableRegistryAccess(listOf(BuiltInRegistries.BLOCK)) }
private val registry by lazy { registryAccess.lookupOrThrow(Registries.BLOCK) }
internal fun loadBlocks(identifiers: MutableSet<Identifier>): Set<Block> {
    val minecraftIdentifiers: MutableSet<MinecraftIdentifier> = mutableSetOf()
    identifiers.forEach { (minecraftIdentifier, isTag) ->
        if (!isTag) minecraftIdentifiers.add(minecraftIdentifier)
        else minecraftIdentifiers.addAll(minecraftIdentifier.getTagEntries())
    }
    return minecraftIdentifiers.mapTo(mutableSetOf()) {
        registry.get(it).get().value()
    }
}

private fun MinecraftIdentifier.getTagEntries(): List<MinecraftIdentifier> {
    val tagKey = TagKey.create(Registries.BLOCK, this)
    val entries = registry.getTagOrEmpty(tagKey)
    val locations = entries.map { it.unwrapKey().get().identifier() }

    return locations
}

internal fun reloadCache() {
    allowedSupportingBlocks = loadBlocks(config.boosting.allowedSupportingBlocks)
    allowedCollidingBlocks = loadBlocks(config.boosting.allowedCollidingBlocks)
}