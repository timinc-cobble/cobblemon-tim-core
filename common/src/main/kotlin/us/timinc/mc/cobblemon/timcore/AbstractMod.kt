package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.permission.PermissionLevel
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty
import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.api.scheduling.afterOnServer
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

abstract class AbstractMod<T : AbstractConfig>(
    @Suppress("MemberVisibilityCanBePrivate") val modId: String,
    private val configClass: Class<T>,
) {
    @Suppress("MemberVisibilityCanBePrivate")
    var debugger: Debugger<T>

    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var config: T
    val commands: MutableList<LiteralArgumentBuilder<CommandSourceStack>> = mutableListOf()
    val customPokemonProperties: MutableList<CustomPokemonPropertyType<*>> = mutableListOf()

    init {
        reloadConfig()
        debugger = Debugger(modId, config)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun registerCommand(cmd: LiteralArgumentBuilder<CommandSourceStack>, noPrefix: Boolean = false) {
        commands.add(if (noPrefix) cmd else literal(modId).then(cmd))
    }

    @Suppress("unused")
    fun <R : CustomPokemonProperty, T : CustomPokemonPropertyType<R>> registerCustomPokemonProperty(prop: T): T {
        customPokemonProperties.add(prop)
        return prop
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun reloadConfig() {
        config = ConfigBuilder.load(configClass, modId)
    }

    private val reloadConfigCommand: LiteralArgumentBuilder<CommandSourceStack> =
        literal("reload").requires { it.hasPermission(PermissionLevel.ALL_COMMANDS.numericalValue) }.executes {
            reloadConfig()
            it.source.player?.sendSystemMessage(Component.literal("Config reloaded."))
            0
        }

    fun wrapUp() {
        var initialized = false
        PlatformEvents.SERVER_STARTED.subscribe { evt ->
            if (initialized) return@subscribe
            initialized = true
            afterOnServer(1, evt.server.overworld()) {
                customPokemonProperties.forEach { CustomPokemonProperty.register(it) }
            }
        }
    }

    @Suppress("unused")
    fun modResource(name: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(modId, name)

    init {
        registerCommand(reloadConfigCommand)
    }
}