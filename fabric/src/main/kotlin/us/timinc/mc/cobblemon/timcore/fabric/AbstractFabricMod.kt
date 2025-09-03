package us.timinc.mc.cobblemon.timcore.fabric

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.commands.CommandSourceStack
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import us.timinc.mc.cobblemon.timcore.AbstractMod

abstract class AbstractFabricMod(@Suppress("MemberVisibilityCanBePrivate") val mod: AbstractMod<*>) : ModInitializer {
    init {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            registerCommands(dispatcher)
        }
        mod.items.entries.forEach { (k, v) -> registerItem(k, v.item) }
        mod.blocks.entries.forEach { (k, v) ->
            registerBlock(k, v.block)
            v.item?.let { registerItem(k, it) }
        }

//        TODO: Figure out why adding items to creative tabs is breaking on Fabric.
//        mod.items.values.map { it.tab }.toSet().forEach { tabToAddTo ->
//            ItemGroupEvents.modifyEntriesEvent(tabToAddTo).register { addingTab ->
//                for (container in mod.items.values) {
//                    if (container.tab != tabToAddTo) continue
//                    addingTab.accept(container.item)
//                }
//            }
//        }
//        mod.blocks.values.map { it.tab }.toSet().forEach { tabToAddTo ->
//            ItemGroupEvents.modifyEntriesEvent(tabToAddTo).register { addingTab ->
//                for (container in mod.blocks.values) {
//                    if (container.tab != tabToAddTo) continue
//                    container.item?.let { addingTab.accept(it) }
//                }
//            }
//        }
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