package us.timinc.mc.cobblemon.timcore.matcher.piece

import us.timinc.mc.cobblemon.timcore.PokemonMatcher
import us.timinc.mc.cobblemon.timcore.condition.PokemonPropertiesCondition
import us.timinc.mc.cobblemon.timcore.matcher.MatcherRawData

class PropertiesPiece : Piece<PokemonPropertiesCondition> {
    override fun condition(raw: MatcherRawData): PokemonPropertiesCondition? =
        raw.asString(PokemonMatcher.pieces.flatMap { it.allKeys }.toSet())
            .takeIf { it.isNotBlank() }
            ?.let(::PokemonPropertiesCondition)

    override val allKeys: Set<String>
        get() = emptySet()
}
