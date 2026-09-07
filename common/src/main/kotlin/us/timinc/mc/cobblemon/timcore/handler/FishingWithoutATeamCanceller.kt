package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.api.events.fishing.BobberSpawnPokemonEvent
import com.cobblemon.mod.common.util.party
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCore

object FishingWithoutATeamCanceller : AbstractHandler<BobberSpawnPokemonEvent.Pre>() {
    override fun handle(evt: BobberSpawnPokemonEvent.Pre) {
        if (!TimCore.config.requirePartyToFishPokemon) return

        val player = evt.bobber.owner as? ServerPlayer ?: return
        if (!player.party().isEmpty()) return

        evt.bobber.discard()
        evt.cancel()
    }
}
