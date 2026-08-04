package dev.nyon.bbm.config

import com.mojang.logging.LogUtils
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

data class BlockFilters(
    val supporting: Set<Block>,
    val colliding: Set<Block>
)

private data class CachedFilters(
    val supportingIds: Set<Identifier>,
    val collidingIds: Set<Identifier>,
    val filters: BlockFilters
)

private val logger = LogUtils.getLogger()
private val registryAccess by lazy { RegistryAccess.ImmutableRegistryAccess(listOf(BuiltInRegistries.BLOCK)) }
private val registry by lazy { registryAccess.lookupOrThrow(Registries.BLOCK) }

@Volatile
private var cache: CachedFilters? = null

fun filtersFor(snapshot: GameplayConfigSnapshot): BlockFilters {
    val current = cache
    if (current != null &&
        current.supportingIds == snapshot.allowedSupportingBlocks &&
        current.collidingIds == snapshot.allowedCollidingBlocks
    ) return current.filters

    return synchronized(registry) {
        val filters = BlockFilters(
            loadBlocks(snapshot.allowedSupportingBlocks),
            loadBlocks(snapshot.allowedCollidingBlocks)
        )
        cache = CachedFilters(snapshot.allowedSupportingBlocks, snapshot.allowedCollidingBlocks, filters)
        filters
    }
}

internal fun loadBlocks(identifiers: Set<Identifier>): Set<Block> = buildSet {
    identifiers.forEach { entry ->
        val location = entry.original
        if (location == null) {
            logger.warn("Ignoring invalid BBM block identifier '{}'", entry)
            return@forEach
        }

        if (entry.isTag) {
            val tag = TagKey.create(Registries.BLOCK, location)
            val entries = registry.getTagOrEmpty(tag).toList()
            if (entries.isEmpty()) logger.warn("Ignoring unknown or empty BBM block tag '{}'", entry)
            entries.forEach { holder -> add(holder.value()) }
        } else {
            val block = registry.get(location).orElse(null)?.value()
            if (block == null) logger.warn("Ignoring unknown BBM block identifier '{}'", entry)
            else add(block)
        }
    }
}

internal fun reloadCache(snapshot: GameplayConfigSnapshot? = null) {
    cache = null
    if (snapshot != null) filtersFor(snapshot)
}
