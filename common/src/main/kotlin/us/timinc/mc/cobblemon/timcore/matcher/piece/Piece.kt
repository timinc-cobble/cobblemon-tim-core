package us.timinc.mc.cobblemon.timcore.matcher.piece

import com.cobblemon.mod.common.pokemon.Pokemon
import us.timinc.mc.cobblemon.timcore.matcher.MatcherRawData
import java.util.function.Predicate

interface Piece<P : Predicate<Pokemon>> {
    fun condition(raw: MatcherRawData): P?
    val allKeys: Set<String>
}