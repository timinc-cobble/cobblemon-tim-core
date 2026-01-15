package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.Codec
import us.timinc.mc.cobblemon.timcore.matcher.MatcherRawData
import us.timinc.mc.cobblemon.timcore.matcher.piece.Piece
import java.util.function.Predicate

data class PokemonMatcher(
    val raw: MatcherRawData,
) {
    companion object {
        @Suppress("unused")
        val STRING_CODEC: Codec<PokemonMatcher> = Codec.STRING.xmap(
            { parse(it) },
            { it.asString() }
        )

        val pieces: MutableList<Piece<*>> = mutableListOf()

        fun <P : Predicate<Pokemon>> registerPiece(piece: Piece<P>): Piece<P> {
            pieces.add(piece)
            return piece
        }

        fun parse(string: String): PokemonMatcher = PokemonMatcher(MatcherRawData(string))
    }

    val matchOne: Boolean
        get() = raw.getBoolean(setOf("match_one")) ?: false

    val conditions: List<Predicate<Pokemon>>
        get() = pieces.map { it.condition(raw) }

    fun matches(pokemon: Pokemon): Boolean {
        if (conditions.isEmpty()) return true
        return if (matchOne) conditions.any { it.test(pokemon) } else conditions.all { it.test(pokemon) }
    }

    fun asString(): String = raw.asString()
}
