package dev.nyon.bbm.config

import dev.isxander.yacl3.dsl.*
import dev.nyon.konfig.config.saveConfig
import net.minecraft.client.gui.screens.Screen

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

        val boostUnderwater by rootOptions.registering {
            binding(true, { config.boostUnderwater }, { config.boostUnderwater = it })
            controller = tickBox()
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val boostOnBlocks by rootOptions.registering {
            binding(true, { config.boostOnBlocks }, { config.boostOnBlocks = it })
            controller = tickBox()
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val boostOnIce by rootOptions.registering {
            binding(true, { config.boostOnIce }, { config.boostOnIce = it })
            controller = tickBox()
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val boostOnWater by rootOptions.registering {
            binding(true, { config.boostOnWater }, { config.boostOnWater = it })
            controller = tickBox()
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val onlyForPlayers by rootOptions.registering {
            binding(true, { config.onlyForPlayers }, { config.onlyForPlayers = it })
            controller = tickBox()
            descriptionBuilder {
                addDefaultText(1)
            }
        }

        val extraCollisionDetectionRange by rootOptions.registering {
            binding(0.5, { config.extraCollisionDetectionRange }, { config.extraCollisionDetectionRange = it })
            controller = numberField(0.0)
            descriptionBuilder {
                addDefaultText(1)
            }
        }
    }

    save { saveConfig(config) }
}.generateScreen(parent)