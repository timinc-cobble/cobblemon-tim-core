package us.timinc.mc.cobblemon.timcore.matcher.piece

import com.cobblemon.mod.common.pokemon.Pokemon
import us.timinc.mc.cobblemon.timcore.PokemonMatcher
import us.timinc.mc.cobblemon.timcore.condition.RangeCondition
import us.timinc.mc.cobblemon.timcore.matcher.MatcherRawData

class IntRangePiece(
    val getter: (pokemon: Pokemon) -> Int,
    val minKeys: Set<String>? = null,
    val maxKeys: Set<String>? = null,
    val minValueBackup: Int = Int.MIN_VALUE,
    val maxValueBackup: Int = Int.MAX_VALUE,
) : Piece<RangeCondition<Pokemon, Int>> {
    override fun condition(raw: MatcherRawData): RangeCondition<Pokemon, Int>? {
        val min = minKeys?.let(raw::getInt)
        val max = maxKeys?.let(raw::getInt)
        if (min == null && max == null) return null

        return RangeCondition(
            min ?: minValueBackup,
            max ?: maxValueBackup,
            getter,
        )
    }

    @Suppress("unused")
    fun apply(matcher: PokemonMatcher, min: Int? = null, max: Int? = null) {
        min?.let { min -> minKeys?.let { minKeys -> matcher.raw.setInt(min, minKeys) } }
        max?.let { max -> maxKeys?.let { maxKeys -> matcher.raw.setInt(max, maxKeys) } }
    }

    override val allKeys: Set<String>
        get() = (minKeys ?: emptySet()) + (maxKeys ?: emptySet())
}