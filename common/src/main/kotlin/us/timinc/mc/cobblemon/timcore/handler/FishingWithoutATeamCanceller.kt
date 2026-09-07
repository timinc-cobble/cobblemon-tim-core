package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.api.events.entity.SpawnEvent
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.party
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCore
import us.timinc.mc.cobblemon.timcore.getType

object FishingWithoutATeamCanceller : AbstractHandler<SpawnEvent<PokemonEntity>>() {
    override fun handle(evt: SpawnEvent<PokemonEntity>) {
        if (!TimCore.config.requirePartyToFishPokemon) return
        if (evt.spawnablePosition.spawner.getType() != TimCore.DataKeys.SpawnerTypes.FISHING) return

        val player = evt.spawnablePosition.cause.entity as? ServerPlayer
        if (player != null && player.party().all {
                // The Pokémon in the slot can be null. This is silly that it doesn't know that.
                @Suppress("SENSELESS_COMPARISON")
                it == null
            }) {
            evt.cancel()
        }
    }
}
