package us.timinc.mc.cobblemon.timcore.mixin.helper

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import us.timinc.mc.cobblemon.timcore.ReservationType
import us.timinc.mc.cobblemon.timcore.TimCore
import us.timinc.mc.cobblemon.timcore.isReservedFor

object CantHurtPokemonEntityHelper {
    fun cantBeHurtInGeneral(damageSource: DamageSource): Boolean =
        TimCore.config.pokemonEntitiesAreInvulnerable
                && !damageSource.isCreativePlayer
                && !damageSource.`is`(DamageTypes.GENERIC_KILL)

    fun cantBeHurtBecauseOwned(pokemonEntity: PokemonEntity, damageSource: DamageSource): Boolean {
        if (!TimCore.config.reservedPokemonEntitiesAreInvulnerable) return false
        val player = damageSource.entity as? ServerPlayer ?: return false
        val pokemon = pokemonEntity.pokemon
        if (pokemon.isReservedFor(player) == ReservationType.RESERVED_FOR_OTHER) {
            player.sendSystemMessage(TimCore.TranslationComponents.reserved(pokemon))
            return true
        }
        return false
    }
}