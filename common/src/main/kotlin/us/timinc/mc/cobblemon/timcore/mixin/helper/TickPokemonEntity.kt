package us.timinc.mc.cobblemon.timcore.mixin.helper

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import us.timinc.mc.cobblemon.timcore.TimCoreEvents
import us.timinc.mc.cobblemon.timcore.event.PokemonEntityTickedEvent

object TickPokemonEntity {
    fun tick(pokemon: PokemonEntity) {
        TimCoreEvents.POKEMON_TICKED.post(PokemonEntityTickedEvent(pokemon))
    }
}