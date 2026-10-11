package us.timinc.mc.cobblemon.timcore.fabric

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import us.timinc.mc.cobblemon.timcore.AbstractMod
import us.timinc.mc.cobblemon.timcore.CommandArgumentContainer

abstract class AbstractFabricMod(@Suppress("MemberVisibilityCanBePrivate") val mod: AbstractMod<*>) : ModInitializer {
    init {
        fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>>
                CommandArgumentContainer<A, T>.register() {
            ArgumentTypeRegistry.registerArgumentType(
                identifier, argumentClass, info
            )
        }
        mod.commandArguments.values.forEach { it.register() }
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            registerCommands(dispatcher)
        }
        mod.items.entries.forEach { (k, v) -> registerItem(k, v.item) }
        mod.blocks.entries.forEach { (k, v) ->
            registerBlock(k, v.block)
            v.item?.let { registerItem(k, it) }
        }
        mod.blockEntityTypes.entries.forEach { (k, v) ->
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, k, v.type)
        }
        mod.menuTypes.entries.forEach { (k, v) ->
            Registry.register(BuiltInRegistries.MENU, k, v.type)
        }

        (mod.items.values.mapNotNull { it.tab } + mod.blocks.values.mapNotNull { it.tab })
            .toSet().forEach { tab ->
                ItemGroupEvents.modifyEntriesEvent(tab).register { entries ->
                    mod.items.values.filter { it.tab == tab }.forEach { entries.accept(it.item) }
                    mod.blocks.values.filter { it.tab == tab }.forEach { container ->
                        container.item?.let { entries.accept(it) }
                    }
                }
            }
        registerReloadListeners()
        mod.wrapUp()
    }

    private fun registerBlock(resourceLocation: ResourceLocation, block: Block) {
        Registry.register(BuiltInRegistries.BLOCK, resourceLocation, block)
    }

    private fun registerItem(resourceLocation: ResourceLocation, item: Item) {
        Registry.register(BuiltInRegistries.ITEM, resourceLocation, item)
    }

    private fun registerCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
        mod.commands.forEach { cmdContainer ->
            dispatcher.register(cmdContainer.built)
        }
    }

    private fun registerReloadListeners() {
        mod.reloadListeners.forEach { listener ->
            ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(
                FabricReloadListener(listener, this)
            )
        }
    }
}
