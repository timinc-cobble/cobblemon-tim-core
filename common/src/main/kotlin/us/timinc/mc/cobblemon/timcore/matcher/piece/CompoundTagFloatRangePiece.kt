package us.timinc.mc.cobblemon.timcore.matcher.piece

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.nbt.CompoundTag
import us.timinc.mc.cobblemon.timcore.condition.CompoundTagFloatRangeCondition
import us.timinc.mc.cobblemon.timcore.matcher.MatcherRawData

class CompoundTagFloatRangePiece(
    val getter: (Pokemon) -> CompoundTag,
    val propsKeys: Set<String>? = null,
    val propsBackup: Map<List<String>, Pair<Float, Float>> = emptyMap(),
    val matchAnyPropKeys: Set<String>? = null,
    val matchAnyPropBackup: Boolean = false,
) : Piece<CompoundTagFloatRangeCondition<Pokemon>> {
    override fun condition(raw: MatcherRawData): CompoundTagFloatRangeCondition<Pokemon> =
        CompoundTagFloatRangeCondition<Pokemon>(
            propsKeys?.let(raw::getFloatPairMap) ?: propsBackup,
            matchAnyPropKeys?.let(raw::getBoolean) ?: matchAnyPropBackup,
            getter
        )

    override val allKeys: Set<String>
        get() = (propsKeys ?: emptySet()) + (matchAnyPropKeys ?: emptySet())
}