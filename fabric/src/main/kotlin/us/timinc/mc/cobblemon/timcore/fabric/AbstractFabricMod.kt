package us.timinc.mc.cobblemon.timcore.fabric

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import us.timinc.mc.cobblemon.timcore.AbstractMod

abstract class AbstractFabricMod(@Suppress("MemberVisibilityCanBePrivate") val mod: AbstractMod<*>) : ModInitializer {
    init {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            registerCommands(dispatcher)
        }
        mod.wrapUp()
    }

    private fun registerCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
        mod.commands.forEach(dispatcher::register)
    }
}