package us.timinc.mc.cobblemon.timcore.mixin.helper

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.activestate.SentOutState
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB

object SendOutResurrectedPokemonHelper {
    fun handle(pokemon: Pokemon, world: Level, pos: BlockPos) {
        if (world !is ServerLevel) return

        val matchingPokemonEntity =
            world.getEntitiesOfClass(PokemonEntity::class.java, AABB.ofSize(pos.center, 10.0, 10.0, 10.0))
                .firstOrNull { entity ->
                    entity.pokemon == pokemon
                } ?: return

        pokemon.state = SentOutState(matchingPokemonEntity)
    }
}