package us.timinc.mc.cobblemon.timcore

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.network.chat.Component

abstract class AbstractMod<T : AbstractConfig>(
    @Suppress("MemberVisibilityCanBePrivate") val modId: String,
    private val configClass: Class<T>,
) {
    @Suppress("MemberVisibilityCanBePrivate")
    var debugger: Debugger<T>

    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var config: T
    val commands: MutableList<LiteralArgumentBuilder<CommandSourceStack>> = mutableListOf()

    init {
        reloadConfig()
        debugger = Debugger(modId, config)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun registerCommand(cmd: LiteralArgumentBuilder<CommandSourceStack>) {
        commands.add(cmd)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun reloadConfig() {
        config = ConfigBuilder.load(configClass, modId)
    }

    private val reloadConfigCommand: LiteralArgumentBuilder<CommandSourceStack> =
        literal(modId).then(literal("reload").executes {
            reloadConfig()
            it.source.player?.sendSystemMessage(Component.literal("Config reloaded."))
            0
        })

    init {
        registerCommand(reloadConfigCommand)
    }
}