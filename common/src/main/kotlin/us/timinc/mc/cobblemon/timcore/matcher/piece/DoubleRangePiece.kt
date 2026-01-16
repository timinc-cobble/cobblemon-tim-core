package us.timinc.mc.cobblemon.timcore.matcher.piece

import com.cobblemon.mod.common.pokemon.Pokemon
import us.timinc.mc.cobblemon.timcore.PokemonMatcher
import us.timinc.mc.cobblemon.timcore.condition.RangeCondition
import us.timinc.mc.cobblemon.timcore.matcher.MatcherRawData

class DoubleRangePiece(
    val getter: (Pokemon) -> Double,
    val minKeys: Set<String>? = null,
    val maxKeys: Set<String>? = null,
    val minValueBackup: Double = Double.MIN_VALUE,
    val maxValueBackup: Double = Double.MAX_VALUE,
) : Piece<RangeCondition<Pokemon, Double>> {
    override fun condition(raw: MatcherRawData): RangeCondition<Pokemon, Double>? {
        val min = minKeys?.let(raw::getDouble)
        val max = maxKeys?.let(raw::getDouble)
        if (min == null && max == null) return null

        return RangeCondition(
            min ?: minValueBackup,
            max ?: maxValueBackup,
            getter,
        )
    }

    @Suppress("unused")
    fun apply(matcher: PokemonMatcher, min: Double? = null, max: Double? = null) {
        min?.let { min -> minKeys?.let { minKeys -> matcher.raw.setDouble(min, minKeys) } }
        max?.let { max -> maxKeys?.let { maxKeys -> matcher.raw.setDouble(max, maxKeys) } }
    }

    override val allKeys: Set<String>
        get() = (minKeys ?: emptySet()) + (maxKeys ?: emptySet())
}