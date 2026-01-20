package us.timinc.mc.cobblemon.timcore.matcher.piece

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.timcore.condition.ItemTagCondition
import us.timinc.mc.cobblemon.timcore.matcher.MatcherRawData

class ItemTagPiece(
    val getter: (Pokemon) -> ItemStack,
    val tagKeys: Set<String>?,
) : Piece<ItemTagCondition<Pokemon>> {
    override fun condition(raw: MatcherRawData): ItemTagCondition<Pokemon>? {
        val tag = tagKeys?.let(raw::getString) ?: return null

        return ItemTagCondition(tag.asIdentifierDefaultingNamespace(), getter)
    }

    override val allKeys: Set<String>
        get() = tagKeys ?: emptySet()
}