package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.permission.PermissionLevel
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty
import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.api.scheduling.afterOnServer
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import us.timinc.mc.cobblemon.timcore.command.ConfigReloadCommand

abstract class AbstractMod<T : AbstractConfig>(
    @Suppress("MemberVisibilityCanBePrivate") val modId: String,
    private val configClass: Class<T>,
) {
    @Suppress("MemberVisibilityCanBePrivate")
    var debugger: Debugger<T>

    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var config: T

    @Suppress("MemberVisibilityCanBePrivate")
    val commands: MutableList<AbstractCommand<*>> = mutableListOf()

    @Suppress("MemberVisibilityCanBePrivate")
    val customPokemonProperties: MutableList<CustomPokemonPropertyType<*>> = mutableListOf()

    @Suppress("MemberVisibilityCanBePrivate")
    val reloadListeners: MutableList<AbstractReloadListener> = mutableListOf()

    val items: MutableMap<ResourceLocation, ItemContainer<out Item>> = mutableMapOf()
    val blocks: MutableMap<ResourceLocation, BlockContainer<out Block>> = mutableMapOf()

    init {
        reloadConfig()
        debugger = Debugger(modId, config)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun <T> registerCommand(command: AbstractCommand<T>): AbstractCommand<T> {
        commands.add(command)
        return command
    }

    @Suppress("unused")
    fun <R : CustomPokemonProperty, T : CustomPokemonPropertyType<R>> registerCustomPokemonProperty(prop: T): T {
        customPokemonProperties.add(prop)
        return prop
    }

    @Suppress("unused")
    fun <T : AbstractReloadListener> registerReloadListener(listener: T): T {
        reloadListeners.add(listener)
        return listener
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun <T : Item> registerItem(name: String, container: ItemContainer<T>): ItemContainer<T> {
        items[modResource(name)] = container
        return container
    }

    fun <T : Block> registerBlock(name: String, container: BlockContainer<T>): BlockContainer<T> {
        blocks[modResource(name)] = container
        return container
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun reloadConfig() {
        config = ConfigBuilder.load(configClass, modId)
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
        registerCommand(ConfigReloadCommand(this))
    }
}