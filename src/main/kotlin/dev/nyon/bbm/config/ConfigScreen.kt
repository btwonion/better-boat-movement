package dev.nyon.bbm.config

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder
import dev.nyon.bbm.config
import dev.nyon.konfig.config.saveConfig
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

fun generateYaclScreen(parent: Screen?): Screen {
    val builder = YetAnotherConfigLib.createBuilder()

    builder.title(Component.translatable("menu.bbm.config.title"))
    builder.appendCategory()

    builder.save { saveConfig(config) }
    val yacl = builder.build()
    return yacl.generateScreen(parent)
}

private fun YetAnotherConfigLib.Builder.appendCategory() =
    category(
        ConfigCategory.createBuilder()
            .name(Component.translatable("menu.bbm.config.category.title"))
            .option(
                Option.createBuilder<Float>()
                    .name(Component.translatable("menu.bbm.config.category.ground_step_height.title"))
                    .description(OptionDescription.of(Component.translatable("menu.bbm.config.category.ground_step_height.description")))
                    .binding(config.groundStepHeight, { config.groundStepHeight }, { config.groundStepHeight = it })
                    .controller(FloatFieldControllerBuilder::create)
                    .build()
            )
            .option(
                Option.createBuilder<Float>()
                    .name(Component.translatable("menu.bbm.config.category.water_step_height.title"))
                    .description(OptionDescription.of(Component.translatable("menu.bbm.config.category.water_step_height.description")))
                    .binding(config.waterStepHeight, { config.waterStepHeight }, { config.waterStepHeight = it })
                    .controller(FloatFieldControllerBuilder::create)
                    .build()
            )
            .option(
                Option.createBuilder<Float>()
                    .name(Component.translatable("menu.bbm.config.category.player_eject_ticks.title"))
                    .description(OptionDescription.of(Component.translatable("menu.bbm.config.category.player_eject_ticks.description")))
                    .binding(config.playerEjectTicks, { config.playerEjectTicks }, { config.playerEjectTicks = it })
                    .controller(FloatFieldControllerBuilder::create)
                    .build()
            )
            .build()
    )
