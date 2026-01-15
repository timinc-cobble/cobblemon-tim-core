package us.timinc.mc.cobblemon.timcore.matcher.piece

import us.timinc.mc.cobblemon.timcore.condition.PokemonPropertiesCondition
import us.timinc.mc.cobblemon.timcore.matcher.MatcherRawData

class PropertiesPiece() : Piece<PokemonPropertiesCondition> {
    override fun condition(raw: MatcherRawData): PokemonPropertiesCondition =
        PokemonPropertiesCondition(raw.asString())

    override val allKeys: Set<String>
        get() = emptySet()
}