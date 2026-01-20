package us.timinc.mc.cobblemon.timcore.matcher.piece

import com.cobblemon.mod.common.pokemon.Pokemon
import us.timinc.mc.cobblemon.timcore.condition.BooleanCondition
import us.timinc.mc.cobblemon.timcore.matcher.MatcherRawData

class BooleanPiece(
    val getter: (Pokemon) -> Boolean,
    val desiredKeys: Set<String>? = null,
) : Piece<BooleanCondition<Pokemon>> {
    override fun condition(raw: MatcherRawData): BooleanCondition<Pokemon> {
        val desired = desiredKeys?.let(raw::getBoolean)

        return BooleanCondition(desired, getter)
    }

    override val allKeys: Set<String>
        get() = desiredKeys ?: emptySet()
}