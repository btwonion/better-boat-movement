package dev.nyon.bbm.config.screen

import dev.isxander.yacl3.api.ListOption
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.dsl.*
import dev.nyon.bbm.config.Identifier
import dev.nyon.bbm.config.IdentifierSerializer
import dev.nyon.bbm.config.config
import dev.nyon.bbm.config.reloadCache
import dev.nyon.bbm.extensions.Status
import dev.nyon.konfig.config.saveConfig
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

fun generateYaclScreen(parent: Screen?): Screen = YetAnotherConfigLib("bbm") {
    val general by categories.registering {
        val stepHeight by rootOptions.registering {
            binding(1f, { config.stepHeight }, { config.stepHeight = it })
            controller = numberField(0f)
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val playerEjectTicks by rootOptions.registering {
            binding(0.2f, { config.playerEjectTicks }, { config.playerEjectTicks = it })
            controller = numberField(0f, 10000f)
            descriptionBuilder {
                addDefaultText(1)
            }
        }

    }

    val boosting by categories.registering {
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
                        config.boosting.allowedSupportingBlocks = list.mapNotNull { entry ->
                            runCatching { IdentifierSerializer.decodeFromString(entry) }.getOrNull()
                        }.toMutableSet()
                    }
                )
                .initial("")
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
                        config.boosting.allowedCollidingBlocks = list.mapNotNull { entry ->
                            runCatching { IdentifierSerializer.decodeFromString(entry) }.getOrNull()
                        }.toMutableSet()
                    }
                )
                .initial("")
                .build()
        )
        
        val onlyForPlayers by rootOptions.registering {
            binding(true, { config.boosting.onlyForPlayers }, { config.boosting.onlyForPlayers = it })
            controller = tickBox()
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val extraCollisionDetectionRange by rootOptions.registering {
            binding(0.5, { config.boosting.extraCollisionDetectionRange }, { config.boosting.extraCollisionDetectionRange = it })
            controller = numberField(0.0)
            descriptionBuilder {
                addDefaultText(1)
            }
        }
    }
    
    val keybind by categories.registering {
        val allowJumpKeybind by rootOptions.registering {
            binding(false, { config.keybind.allowJumpKeybind }, { config.keybind.allowJumpKeybind = it })
            controller = tickBox()
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val keybindJumpHeightMultiplier by rootOptions.registering {
            binding(2.0, { config.keybind.keybindJumpHeightMultiplier }, { config.keybind.keybindJumpHeightMultiplier = it })
            controller = numberField(0.0)
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val onlyKeybindJumpOnGroundOrWater by rootOptions.registering {
            binding(true, { config.keybind.onlyKeybindJumpOnGroundOrWater }, { config.keybind.onlyKeybindJumpOnGroundOrWater = it })
            controller = tickBox()
            descriptionBuilder {
                addDefaultText(1)
            }
        }
    }

    save {
        reloadCache()
        saveConfig(config)
    }
}.generateScreen(parent)