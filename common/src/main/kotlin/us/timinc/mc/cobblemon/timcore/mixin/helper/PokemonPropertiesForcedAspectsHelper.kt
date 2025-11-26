package us.timinc.mc.cobblemon.timcore.mixin.helper

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon

object PokemonPropertiesForcedAspectsHelper {
    fun getNewAspects(pokemon: Pokemon, props: PokemonProperties) {
        pokemon.forcedAspects += props.aspects
    }
}