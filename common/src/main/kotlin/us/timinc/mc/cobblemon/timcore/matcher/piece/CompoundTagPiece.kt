package us.timinc.mc.cobblemon.timcore.matcher.piece

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.nbt.CompoundTag
import us.timinc.mc.cobblemon.timcore.condition.CompoundTagCondition
import us.timinc.mc.cobblemon.timcore.matcher.MatcherRawData

class CompoundTagPiece(
    val getter: (Pokemon) -> CompoundTag,
    val propsKeys: Set<String>? = null,
    val propsBackup: Map<List<String>, String> = emptyMap(),
    val matchAnyPropKeys: Set<String>? = null,
    val matchAnyPropBackup: Boolean = false,
) : Piece<CompoundTagCondition<Pokemon>> {
    override fun condition(raw: MatcherRawData): CompoundTagCondition<Pokemon> = CompoundTagCondition(
        propsKeys?.let(raw::getStringMap) ?: propsBackup,
        matchAnyPropKeys?.let(raw::getBoolean) ?: matchAnyPropBackup,
        getter
    )

    override val allKeys: Set<String>
        get() = (propsKeys ?: emptySet()) + (matchAnyPropKeys ?: emptySet())
}