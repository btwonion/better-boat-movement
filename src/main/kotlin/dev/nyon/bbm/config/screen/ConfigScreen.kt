package dev.nyon.bbm.config.screen

import dev.isxander.yacl3.api.ListOption
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.dsl.*
import dev.nyon.bbm.config.Identifier
import dev.nyon.bbm.config.IdentifierSerializer
import dev.nyon.bbm.config.ConfigRepository
import dev.nyon.bbm.config.GameplayConfig
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.vehicle.boat.AbstractBoat.Status

fun generateYaclScreen(parent: Screen?): Screen {
    val remote = ConfigRepository.remoteSnapshot
    val readOnly = remote != null && Minecraft.getInstance().singleplayerServer == null
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
            binding(0.35f, { config.stepHeight }, { config.stepHeight = it })
            available = !readOnly
            controller = numberField(0f)
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val playerEjectTicks by rootOptions.registering {
            binding(20f * 10f, { config.playerEjectTicks }, { config.playerEjectTicks = it })
            available = !readOnly
            controller = numberField(0f, 10000f)
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
                        config.boosting.allowedSupportingBlocks =
                            list.map(IdentifierSerializer::decodeFromString).toMutableSet()
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
                        config.boosting.allowedCollidingBlocks =
                            list.map(IdentifierSerializer::decodeFromString).toMutableSet()
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
            binding(0.5, { config.boosting.extraCollisionDetectionRange }, { config.boosting.extraCollisionDetectionRange = it })
            available = !readOnly
            controller = numberField(0.0)
            descriptionBuilder {
                text(
                    Component.translatable("yacl3.config.bbm.category.boosting.root.option.extraCollisionDetectionRange.description"),
                    Component.translatable("yacl3.config.bbm.category.boosting.root.option.extraCollisionDetectionRange.warning")
                        .withStyle(ChatFormatting.BOLD)
                )
            }
        }

        val heightTolerance by rootOptions.registering {
            binding(0.25, { config.boosting.heightTolerance }, { config.boosting.heightTolerance = it })
            available = !readOnly
            controller = numberField(0.0)
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
            binding(1.2, { config.keybind.keybindJumpHeightMultiplier }, { config.keybind.keybindJumpHeightMultiplier = it })
            available = !readOnly
            controller = numberField(0.0)
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
        if (!readOnly) ConfigRepository.save()
    }
    }.generateScreen(parent)
}
