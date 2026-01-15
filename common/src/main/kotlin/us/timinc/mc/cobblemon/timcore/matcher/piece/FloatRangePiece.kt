package us.timinc.mc.cobblemon.timcore.matcher.piece

import com.cobblemon.mod.common.pokemon.Pokemon
import us.timinc.mc.cobblemon.timcore.PokemonMatcher
import us.timinc.mc.cobblemon.timcore.condition.RangeCondition
import us.timinc.mc.cobblemon.timcore.matcher.MatcherRawData

class FloatRangePiece(
    val getter: (Pokemon) -> Float,
    val minKeys: Set<String>? = null,
    val maxKeys: Set<String>? = null,
    val minValueBackup: Float = Float.MIN_VALUE,
    val maxValueBackup: Float = Float.MAX_VALUE,
) : Piece<RangeCondition<Pokemon, Float>> {
    override fun condition(raw: MatcherRawData): RangeCondition<Pokemon, Float>? {
        val min = minKeys?.let(raw::getFloat)
        val max = maxKeys?.let(raw::getFloat)
        if (min == null && max == null) return null

        return RangeCondition(
            min ?: minValueBackup,
            max ?: maxValueBackup,
            getter,
        )
    }

    @Suppress("unused")
    fun apply(matcher: PokemonMatcher, min: Float? = null, max: Float? = null) {
        min?.let { min -> minKeys?.let { minKeys -> matcher.raw.setFloat(min, minKeys) } }
        max?.let { max -> maxKeys?.let { maxKeys -> matcher.raw.setFloat(max, maxKeys) } }
    }

    override val allKeys: Set<String>
        get() = (minKeys ?: emptySet()) + (maxKeys ?: emptySet())
}