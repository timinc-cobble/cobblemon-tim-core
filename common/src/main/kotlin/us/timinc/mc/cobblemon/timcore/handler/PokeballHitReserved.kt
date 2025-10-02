package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.api.events.pokeball.ThrownPokeballHitEvent
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCore
import us.timinc.mc.cobblemon.timcore.TimCore.TranslationComponents.reserved

object PokeballHitReserved : AbstractHandler<ThrownPokeballHitEvent>() {
    override fun handle(evt: ThrownPokeballHitEvent) {
        val owner = (evt.pokeBall.owner as? ServerPlayer) ?: return
        val pokemon = evt.pokemon.pokemon
        if (
            pokemon.persistentData.contains(TimCore.DataKeys.RESERVED_FOR)
            && !TimCore.CustomPokemonProperties.RESERVED_FOR.pokemonMatcher(pokemon, owner.stringUUID)
        ) {
            owner.sendSystemMessage(reserved(pokemon, owner))
            evt.cancel()
        }
    }
}