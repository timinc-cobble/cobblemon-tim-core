package us.timinc.mc.cobblemon.timcore.matcher.piece

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.nbt.CompoundTag
import us.timinc.mc.cobblemon.timcore.PokemonMatcher
import us.timinc.mc.cobblemon.timcore.condition.CompoundTagCondition
import us.timinc.mc.cobblemon.timcore.matcher.MatcherRawData

class CompoundTagPiece(
    val getter: (Pokemon) -> CompoundTag,
    val propsKeys: Set<String>? = null,
    val matchAnyPropKeys: Set<String>? = null,
    val matchAnyPropBackup: Boolean = false,
) : Piece<CompoundTagCondition<Pokemon>> {
    override fun condition(raw: MatcherRawData): CompoundTagCondition<Pokemon>? {
        val props = propsKeys?.let(raw::getStringMap) ?: return null
        return CompoundTagCondition(
            props,
            matchAnyPropKeys?.let(raw::getBoolean) ?: matchAnyPropBackup,
            getter
        )
    }

    @Suppress("unused")
    fun apply(
        matcher: PokemonMatcher,
        props: Map<List<String>, String>? = null,
        matchAnyProp: Boolean? = null,
    ) {
        props?.let { props ->
            propsKeys?.let { propsKeys ->
                matcher.raw.setStringMap(
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