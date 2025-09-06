package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.permission.PermissionLevel
import com.cobblemon.mod.common.util.permission
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component

abstract class AbstractCommand<T>(
    val name: String,
    val permissionLevel: PermissionLevel,
    mod: AbstractMod<*>,
    val args: List<ArgumentBuilder<CommandSourceStack, *>> = listOf(),
    noPrefix: Boolean = false,
) {
    abstract class NoData(
        name: String,
        permissionLevel: PermissionLevel,
        mod: AbstractMod<*>,
        noPrefix: Boolean = false,
    ) :
        AbstractCommand<NoData.Data>(name, permissionLevel, mod, noPrefix = noPrefix) {
        class Data

        abstract fun runWithoutData(rawContext: CommandContext<CommandSourceStack>): Int

        override fun parseContext(ctx: CommandContext<CommandSourceStack>): Data = Data()

        final override fun run(commandContext: Data, rawContext: CommandContext<CommandSourceStack>): Int =
            runWithoutData(rawContext)
    }

    val permission by lazy {
        ModPermission(name, permissionLevel, mod)
    }

    val built: LiteralArgumentBuilder<CommandSourceStack> by lazy {
        if (args.isEmpty()) {
            val defined = Commands.literal(name).permission(permission).executes(::execute)
            return@lazy if (noPrefix) defined else LiteralArgumentBuilder.literal<CommandSourceStack>(mod.modId).then(defined)
        }

        var tail: ArgumentBuilder<CommandSourceStack, *> = args.last().permission(permission).executes(::execute) as ArgumentBuilder<CommandSourceStack, *>

        for (arg in args.reversed().slice(1..<args.size)) {
            tail = arg.then(tail) as ArgumentBuilder<CommandSourceStack, *>
        }

        val defined = Commands.literal(name).then(tail)
        return@lazy if (noPrefix) defined else LiteralArgumentBuilder.literal<CommandSourceStack>(mod.modId).then(defined)
    }

    abstract fun run(commandContext: T, rawContext: CommandContext<CommandSourceStack>): Int
    abstract fun parseContext(ctx: CommandContext<CommandSourceStack>): T

    fun execute(ctx: CommandContext<CommandSourceStack>): Int =
        run(parseContext(ctx), ctx)

    fun giveFeedback(component: Component, ctx: CommandContext<CommandSourceStack>) =
        ctx.source.sendSystemMessage(component)
}