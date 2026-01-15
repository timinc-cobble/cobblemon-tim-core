package us.timinc.mc.cobblemon.timcore.matcher.piece

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.nbt.CompoundTag
import us.timinc.mc.cobblemon.timcore.PokemonMatcher
import us.timinc.mc.cobblemon.timcore.condition.CompoundTagFloatRangeCondition
import us.timinc.mc.cobblemon.timcore.matcher.MatcherRawData

class CompoundTagFloatRangePiece(
    val getter: (Pokemon) -> CompoundTag,
    val propsKeys: Set<String>? = null,
    val matchAnyPropKeys: Set<String>? = null,
    val matchAnyPropBackup: Boolean = false,
) : Piece<CompoundTagFloatRangeCondition<Pokemon>> {
    override fun condition(raw: MatcherRawData): CompoundTagFloatRangeCondition<Pokemon>? {
        val props = propsKeys?.let(raw::getFloatPairMap) ?: return null
        return CompoundTagFloatRangeCondition(
            props,
            matchAnyPropKeys?.let(raw::getBoolean) ?: matchAnyPropBackup,
            getter
        )
    }

    @Suppress("unused")
    fun apply(
        matcher: PokemonMatcher,
        props: Map<List<String>, Pair<Float, Float>>? = null,
        matchAnyProp: Boolean? = null,
    ) {
        props?.let { props ->
            propsKeys?.let { propsKeys ->
                matcher.raw.setFloatPairMap(
                    props,
                    propsKeys,
                )
            }
        }
        matchAnyProp?.let { matchAnyProp ->
            matchAnyPropKeys?.let { matchAnyPropKeys ->
                matcher.raw.setBoolean(
                    matchAnyProp,
                    matchAnyPropKeys,
                )
            }
        }
    }

    override val allKeys: Set<String>
        get() = (propsKeys ?: emptySet()) + (matchAnyPropKeys ?: emptySet())
}