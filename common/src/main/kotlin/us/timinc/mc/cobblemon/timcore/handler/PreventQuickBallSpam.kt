package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.api.events.pokeball.PokemonCatchRateEvent
import com.cobblemon.mod.common.api.pokeball.PokeBalls
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCore.DataKeys.ALREADY_HIT_WITH_QUICK_BALL
import us.timinc.mc.cobblemon.timcore.TimCore.config
import us.timinc.mc.cobblemon.timcore.immuneToQuickBall

object PreventQuickBallSpam : AbstractHandler<PokemonCatchRateEvent>() {
    override fun handle(evt: PokemonCatchRateEvent) {
        if (!config.preventQuickBallSpam) return

        val pokeball = evt.pokeBallEntity.pokeBall
        if (pokeball != PokeBalls.QUICK_BALL) return

        val pokemon = evt.pokemonEntity.pokemon
        if (pokemon.immuneToQuickBall()) {
            evt.catchRate /= PokeBalls.QUICK_BALL.catchRateModifier.modifyCatchRate(1.0F, evt.thrower, pokemon)
        } else {
            pokemon.persistentData.putBoolean(ALREADY_HIT_WITH_QUICK_BALL, true)
        }
    }
}