package dev.nyon.bbm.config

import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import java.util.concurrent.atomic.AtomicReference

data class BlockFilters(
    val supporting: Set<Block>,
    val colliding: Set<Block>
)

private data class CachedFilters(
    val supportingIds: Set<Identifier>,
    val collidingIds: Set<Identifier>,
    val filters: BlockFilters
)

private val registryAccess by lazy { RegistryAccess.ImmutableRegistryAccess(listOf(BuiltInRegistries.BLOCK)) }
private val registry by lazy { registryAccess.lookupOrThrow(Registries.BLOCK) }

private val cache = AtomicReference<CachedFilters?>()

fun filtersFor(snapshot: GameplayConfigSnapshot): BlockFilters {
    while (true) {
        val current = cache.get()
        if (current != null &&
            current.supportingIds == snapshot.allowedSupportingBlocks &&
            current.collidingIds == snapshot.allowedCollidingBlocks
        ) return current.filters

        val updated = CachedFilters(
            snapshot.allowedSupportingBlocks,
            snapshot.allowedCollidingBlocks,
            BlockFilters(
                loadBlocks(snapshot.allowedSupportingBlocks),
                loadBlocks(snapshot.allowedCollidingBlocks)
            )
        )
        if (cache.compareAndSet(current, updated)) return updated.filters
    }
}

internal fun loadBlocks(identifiers: Set<Identifier>): Set<Block> = buildSet {
    identifiers.forEach { entry ->
        if (entry.isTag) {
            val tag = TagKey.create(Registries.BLOCK, entry.original)
            registry.getTagOrEmpty(tag).forEach { holder -> add(holder.value()) }
        } else {
            add(registry.get(entry.original).orElseThrow().value())
        }
    }
}

internal fun reloadCache(snapshot: GameplayConfigSnapshot? = null) {
    cache.set(null)
    if (snapshot != null) filtersFor(snapshot)
}
