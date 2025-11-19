package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.api.events.cooking.PokeSnackSpawnPokemonEvent
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCoreEvents
import us.timinc.mc.cobblemon.timcore.event.EntityDidSpawnEvent

object PokeSnackPokemonDidSpawn : AbstractHandler<PokeSnackSpawnPokemonEvent.Post>() {
    override fun handle(evt: PokeSnackSpawnPokemonEvent.Post) {
        TimCoreEvents.ENTITY_DID_SPAWN.post(
            EntityDidSpawnEvent(
                evt.pokemonEntity, evt.spawnAction
            )
        )
    }
}