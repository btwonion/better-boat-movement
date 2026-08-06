package dev.nyon.bbm.config.screen

import dev.isxander.yacl3.api.ListOption
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.dsl.*
import dev.nyon.bbm.config.Identifier
import dev.nyon.bbm.config.IdentifierSerializer
import dev.nyon.bbm.config.ConfigRepository
import dev.nyon.bbm.config.GameplayConfig
import dev.nyon.bbm.config.GameplayConfigLimits
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.vehicle.boat.AbstractBoat.Status

fun generateYaclScreen(parent: Screen?): Screen {
    val remote = ConfigRepository.remoteSnapshot
    val singleplayer = Minecraft.getInstance().singleplayerServer != null
    val readOnly = remote != null && !singleplayer
    val config: GameplayConfig = if (readOnly) requireNotNull(remote).toMutableConfig() else ConfigRepository.localConfig
    return YetAnotherConfigLib("bbm") {
    val boosting by categories.registering {
        if (readOnly) {
            rootOptions.registerLabel(
                "serverManaged",
                Component.translatable("yacl3.config.bbm.serverManaged")
            )
        }

        val stepHeight by rootOptions.registering {
            binding(
                GameplayConfigLimits.DEFAULT_STEP_HEIGHT,
                { config.stepHeight },
                { config.stepHeight = it }
            )
            available = !readOnly
            controller = numberField(GameplayConfigLimits.MIN_STEP_HEIGHT, GameplayConfigLimits.MAX_STEP_HEIGHT)
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val playerEjectTicks by rootOptions.registering {
            binding(
                GameplayConfigLimits.DEFAULT_PLAYER_EJECT_TICKS,
                { config.playerEjectTicks },
                { config.playerEjectTicks = it }
            )
            available = !readOnly
            controller = numberField(
                GameplayConfigLimits.MIN_PLAYER_EJECT_TICKS,
                GameplayConfigLimits.MAX_PLAYER_EJECT_TICKS
            )
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val boostStates = rootOptions.register(
            "boostStates",
            ListOption.createBuilder<Status>()
                .name(Component.translatable("yacl3.config.bbm.category.boosting.root.option.boostStates"))
                .description(OptionDescription.createBuilder().text(Component.translatable("yacl3.config.bbm.category.boosting.root.option.boostStates.description")).build())
                .controller(enumSwitch())
                .binding(
                    mutableListOf(
                        Status.ON_LAND,
                        Status.IN_WATER,
                        Status.UNDER_FLOWING_WATER,
                        Status.UNDER_WATER
                    ),
                    { config.boosting.boostStates.toList() },
                    { list->
                        config.boosting.boostStates = list.toMutableSet()
                    }
                )
                .maximumNumberOfEntries(Status.entries.size)
                .initial(Status.ON_LAND)
                .available(!readOnly)
                .build()
        )

        val allowedSupportingBlocks = rootOptions.register(
            "allowedSupportingBlocks",
            ListOption.createBuilder<String>()
                .name(Component.translatable("yacl3.config.bbm.category.boosting.root.option.allowedSupportingBlocks"))
                .description(OptionDescription.createBuilder().text(Component.translatable("yacl3.config.bbm.category.boosting.root.option.allowedSupportingBlocks.description")).build())
                .controller(stringField())
                .binding(
                    emptyList(),
                    { config.boosting.allowedSupportingBlocks.map(Identifier::toString) },
                    { list->
                        config.boosting.allowedSupportingBlocks = decodeIdentifiers(list)
                    }
                )
                .initial("")
                .available(!readOnly)
                .build()
        )

        val allowedCollidingBlocks = rootOptions.register(
            "allowedCollidingBlocks",
            ListOption.createBuilder<String>()
                .name(Component.translatable("yacl3.config.bbm.category.boosting.root.option.allowedCollidingBlocks"))
                .description(OptionDescription.createBuilder().text(Component.translatable("yacl3.config.bbm.category.boosting.root.option.allowedCollidingBlocks.description")).build())
                .controller(stringField())
                .binding(
                    emptyList(),
                    { config.boosting.allowedCollidingBlocks.map(Identifier::toString) },
                    { list->
                        config.boosting.allowedCollidingBlocks = decodeIdentifiers(list)
                    }
                )
                .initial("")
                .available(!readOnly)
                .build()
        )
        
        val onlyForPlayers by rootOptions.registering {
            binding(true, { config.boosting.onlyForPlayers }, { config.boosting.onlyForPlayers = it })
            available = !readOnly
            controller = tickBox()
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val extraCollisionDetectionRange by rootOptions.registering {
            binding(
                GameplayConfigLimits.DEFAULT_EXTRA_COLLISION_DETECTION_RANGE,
                { config.boosting.extraCollisionDetectionRange },
                { config.boosting.extraCollisionDetectionRange = it }
            )
            available = !readOnly
            controller = numberField(
                GameplayConfigLimits.MIN_EXTRA_COLLISION_DETECTION_RANGE,
                GameplayConfigLimits.MAX_EXTRA_COLLISION_DETECTION_RANGE
            )
            descriptionBuilder {
                text(
                    Component.translatable("yacl3.config.bbm.category.boosting.root.option.extraCollisionDetectionRange.description"),
                    Component.translatable("yacl3.config.bbm.category.boosting.root.option.extraCollisionDetectionRange.warning")
                        .withStyle(ChatFormatting.BOLD)
                )
            }
        }

        val heightTolerance by rootOptions.registering {
            binding(
                GameplayConfigLimits.DEFAULT_HEIGHT_TOLERANCE,
                { config.boosting.heightTolerance },
                { config.boosting.heightTolerance = it }
            )
            available = !readOnly
            controller = numberField(
                GameplayConfigLimits.MIN_HEIGHT_TOLERANCE,
                GameplayConfigLimits.MAX_HEIGHT_TOLERANCE
            )
            descriptionBuilder {
                addDefaultText(1)
            }
        }
    }
    
    val keybind by categories.registering {
        val allowJumpKeybind by rootOptions.registering {
            binding(false, { config.keybind.allowJumpKeybind }, { config.keybind.allowJumpKeybind = it })
            available = !readOnly
            controller = tickBox()
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val keybindJumpHeightMultiplier by rootOptions.registering {
            binding(
                GameplayConfigLimits.DEFAULT_KEYBIND_JUMP_HEIGHT_MULTIPLIER,
                { config.keybind.keybindJumpHeightMultiplier },
                { config.keybind.keybindJumpHeightMultiplier = it }
            )
            available = !readOnly
            controller = numberField(
                GameplayConfigLimits.MIN_KEYBIND_JUMP_HEIGHT_MULTIPLIER,
                GameplayConfigLimits.MAX_KEYBIND_JUMP_HEIGHT_MULTIPLIER
            )
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val onlyKeybindJumpOnGroundOrWater by rootOptions.registering {
            binding(true, { config.keybind.onlyKeybindJumpOnGroundOrWater }, { config.keybind.onlyKeybindJumpOnGroundOrWater = it })
            available = !readOnly
            controller = tickBox()
            descriptionBuilder {
                addDefaultText(1)
            }
        }
    }

    save {
        if (!readOnly) ConfigRepository.save(syncRemote = singleplayer)
    }
    }.generateScreen(parent)
}

internal fun decodeIdentifiers(entries: List<String>): MutableSet<Identifier> = entries.mapNotNull { entry ->
    runCatching { IdentifierSerializer.decodeFromString(entry) }.getOrNull()
}.toMutableSet()
