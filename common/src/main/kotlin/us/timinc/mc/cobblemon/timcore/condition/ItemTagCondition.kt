package us.timinc.mc.cobblemon.timcore.condition

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import java.util.function.Predicate

class ItemTagCondition<T>(
    val tag: ResourceLocation,
    val getter: (t: T) -> ItemStack,
) : Predicate<T> {
    override fun test(t: T): Boolean =
        getter(t).tags.anyMatch { it.location == tag }
}