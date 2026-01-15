package us.timinc.mc.cobblemon.timcore.command

import com.cobblemon.mod.common.api.permission.PermissionLevel
import com.cobblemon.mod.common.util.party
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import us.timinc.mc.cobblemon.timcore.AbstractCommand
import us.timinc.mc.cobblemon.timcore.PokemonMatcher
import us.timinc.mc.cobblemon.timcore.TimCore

object PokemonMatcherTestCommand : AbstractCommand<PokemonMatcherTestCommand.Data>(
    "pokemon_matcher_test",
    PermissionLevel.ALL_COMMANDS,
    TimCore,
    listOf(
        Commands.argument("slot", IntegerArgumentType.integer(1, 6)),
        Commands.argument("matcher", StringArgumentType.greedyString()),
    )
) {
    override fun run(
        commandContext: Data,
        rawContext: CommandContext<CommandSourceStack>,
    ): Int {
        val player = rawContext.source.player ?: return 0
        val teamMember = player.party().get(commandContext.slot - 1) ?: run {
            player.sendSystemMessage(Component.translatable("Invalid party slot ${commandContext.slot}."))
            return 0
        }
        if (PokemonMatcher.parse(commandContext.matcher).matches(teamMember)) {
            player.sendSystemMessage(Component.translatable("tim_core.command.result.pokemon_matcher_test.success"))
            return Command.SINGLE_SUCCESS
        }
        player.sendSystemMessage(Component.translatable("tim_core.command.result.pokemon_matcher_test.failure"))
        return 0
    }

    override fun parseContext(ctx: CommandContext<CommandSourceStack>): Data =
        Data(
            IntegerArgumentType.getInteger(ctx, "slot"),
            StringArgumentType.getString(ctx, "matcher"),
        )

    class Data(
        val slot: Int,
        val matcher: String,
    )
}