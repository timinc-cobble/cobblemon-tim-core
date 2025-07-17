package us.timinc.mc.cobblemon.timcore.mixin.helper

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.pokeball.ThrownPokeballHitEvent
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.server.level.ServerPlayer

object CancelThrownPokeballInBattleHelper {
    fun checkForCancel(pokeBallEntity: EmptyPokeBallEntity, pokemonEntity: PokemonEntity): Boolean {
        CobblemonEvents.THROWN_POKEBALL_HIT.postThen(
            ThrownPokeballHitEvent(pokeBallEntity, pokemonEntity),
            ifCanceled = {
                return true
            },
            ifSucceeded = {
                return false
            }
        )
        return false
    }
}