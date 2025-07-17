@file:Suppress("unused")

package us.timinc.mc.cobblemon.timcore.neoforge

import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import us.timinc.mc.cobblemon.timcore.AbstractMod

abstract class AbstractNeoForgeMod(@Suppress("MemberVisibilityCanBePrivate") val mod: AbstractMod<*>) {
    init {
        NeoForge.EVENT_BUS.addListener(::registerCommands)
        mod.wrapUp()
    }

    private fun registerCommands(e: RegisterCommandsEvent) {
        val dispatcher = e.dispatcher
        mod.commands.forEach(dispatcher::register)
    }
}