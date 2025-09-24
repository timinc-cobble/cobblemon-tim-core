package us.timinc.mc.cobblemon.timcore.neoforge

import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.AddReloadListenerEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import us.timinc.mc.cobblemon.timcore.AbstractMod
import us.timinc.mc.cobblemon.timcore.CommandArgumentContainer

abstract class AbstractNeoForgeMod(@Suppress("MemberVisibilityCanBePrivate") val mod: AbstractMod<*>) {
    private val commandArgumentTypes = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, mod.modId)

    init {
        fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>>
                CommandArgumentContainer<A, T>.register() {
            commandArgumentTypes.register(identifier.path) { _ ->
                ArgumentTypeInfos.registerByClass(argumentClass, info)
            }
        }
        mod.commandArguments.values.forEach { it.register() }
        commandArgumentTypes.register(MOD_BUS)
        NeoForge.EVENT_BUS.addListener(::registerCommands)
        NeoForge.EVENT_BUS.addListener(::registerReloadListeners)
        MOD_BUS.addListener(::registerItems)
        MOD_BUS.addListener(::registerBlocks)
        MOD_BUS.addListener(::onCreativeTabsModification)
        mod.wrapUp()
    }

    private fun registerItems(e: RegisterEvent) {
        if (e.registry != BuiltInRegistries.ITEM) return
        mod.items.entries.forEach { (k, v) ->
            Registry.register(BuiltInRegistries.ITEM, k, v.item)
        }
        mod.blocks.entries.forEach { (k, v) ->
            v.item?.let { Registry.register(BuiltInRegistries.ITEM, k, it) }
        }
    }

    private fun registerBlocks(e: RegisterEvent) {
        if (e.registry != BuiltInRegistries.BLOCK) return
        mod.blocks.entries.forEach { (k, v) ->
            Registry.register(BuiltInRegistries.BLOCK, k, v.block)
        }
    }

    private fun onCreativeTabsModification(e: BuildCreativeModeTabContentsEvent) {
        for (container in mod.items.values) {
            if (e.tabKey != container.tab) continue
            e.accept(container.item)
        }
        for (container in mod.blocks.values) {
            if (e.tabKey != container.tab) continue
            container.item?.let { e.accept(it) }
        }
    }

    private fun registerCommands(e: RegisterCommandsEvent) {
        val dispatcher = e.dispatcher
        mod.commands.forEach { cmdContainer -> dispatcher.register(cmdContainer.built) }
    }

    private fun registerReloadListeners(e: AddReloadListenerEvent) {
        mod.reloadListeners.forEach { listener ->
            e.addListener(NeoForgeReloadListener(listener))
        }
    }
}