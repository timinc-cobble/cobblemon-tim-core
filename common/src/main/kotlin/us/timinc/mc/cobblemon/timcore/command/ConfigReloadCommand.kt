package us.timinc.mc.cobblemon.timcore.command

import com.cobblemon.mod.common.api.permission.PermissionLevel
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import us.timinc.mc.cobblemon.timcore.AbstractCommand
import us.timinc.mc.cobblemon.timcore.AbstractMod

class ConfigReloadCommand(val mod: AbstractMod<*>) : AbstractCommand.NoData(
    "reload",
    PermissionLevel.ALL_COMMANDS,
    mod
) {
    override fun runWithoutData(rawContext: CommandContext<CommandSourceStack>): Int {
        mod.reloadConfig()
        giveFeedback(Component.literal("Config reloaded."), rawContext)
        return 0
    }
}