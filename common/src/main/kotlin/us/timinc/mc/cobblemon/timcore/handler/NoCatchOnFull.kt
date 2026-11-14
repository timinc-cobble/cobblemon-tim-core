package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.api.events.pokeball.ThrownPokeballHitEvent
import com.cobblemon.mod.common.util.party
import com.cobblemon.mod.common.util.pc
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCore

object NoCatchOnFull : AbstractHandler<ThrownPokeballHitEvent>() {
    override fun handle(evt: ThrownPokeballHitEvent) {
        if (!TimCore.config.noCatchOnFull) return

        val target = evt.pokemon.pokemon

        val debugger = TimCore.debugger.getCaseDebugger()
        fun debug(msg: String) =
            debugger.debug("[noCatchOnFull][${target.uuid}}](${target.getDisplayName().string}}) $msg")

        val owner = (evt.pokeBall.owner as? ServerPlayer) ?: run {
            debug("Could not find ball owner.")
            return
        }

        val hasAvailablePosition = when {
            owner.party().getFirstAvailablePosition() != null -> {
                debug("Found an open party position for the player ${owner.name.string}.")
                true
            }

            owner.pc().getFirstAvailablePosition() != null -> {
                debug("Found an open PC position for the player ${owner.name.string}.")
                true
            }

            else -> false
        }

        if (hasAvailablePosition) return

        debug("Could not find an open position in either the party or the PC of the player, ${owner.name.string}. Cancelled.")
        evt.cancel()
    }
}
