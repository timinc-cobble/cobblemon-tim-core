package us.timinc.mc.cobblemon.timcore.event

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity

data class PokemonEntityTickedEvent(
    val entity: PokemonEntity,
)