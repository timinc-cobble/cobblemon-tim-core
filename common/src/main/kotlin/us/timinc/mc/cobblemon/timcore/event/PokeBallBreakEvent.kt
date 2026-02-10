package us.timinc.mc.cobblemon.timcore.event

import com.cobblemon.mod.common.api.events.Cancelable
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokeball.PokeBall
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity

interface PokeBallBreakEvent {
    val pokeBall: PokeBall
    val reason: String
    val level: ServerLevel
    val owner: Entity?
    val pokemon: PokemonEntity?
    val blockPos: BlockPos?

    companion object {

    }

    class Generic(
        override val pokeBall: PokeBall,
        override val reason: String,
        override val level: ServerLevel,
        override val owner: Entity?,
        override val pokemon: PokemonEntity?,
        override val blockPos: BlockPos?,
    ) : PokeBallBreakEvent {
        fun toPre(): Pre = Pre(pokeBall, reason, level, owner, pokemon, blockPos)
        fun toPost(): Post = Post(pokeBall, reason, level, owner, pokemon, blockPos)
        fun toChance(startingChance: Float) = Chance(pokeBall, reason, level, owner, pokemon, blockPos, startingChance)
        override fun toString(): String = "PokeBall: ${pokeBall.name}, reason: $reason, level: $level, owner: $owner, pokemon: $pokemon, blockPos: $blockPos"
    }

    class Pre(
        override val pokeBall: PokeBall,
        override val reason: String,
        override val level: ServerLevel,
        override val owner: Entity?,
        override val pokemon: PokemonEntity?,
        override val blockPos: BlockPos?,
    ) : PokeBallBreakEvent, Cancelable()

    class Post(
        override val pokeBall: PokeBall,
        override val reason: String,
        override val level: ServerLevel,
        override val owner: Entity?,
        override val pokemon: PokemonEntity?,
        override val blockPos: BlockPos?,
    ) : PokeBallBreakEvent

    class Chance(
        override val pokeBall: PokeBall,
        override val reason: String,
        override val level: ServerLevel,
        override val owner: Entity?,
        override val pokemon: PokemonEntity?,
        override val blockPos: BlockPos?,
        var chance: Float
    ) : PokeBallBreakEvent
}