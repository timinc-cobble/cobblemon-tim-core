package us.timinc.mc.cobblemon.timcore.condition

import net.minecraft.nbt.CompoundTag
import us.timinc.mc.cobblemon.timcore.getCompoundOrNull
import us.timinc.mc.cobblemon.timcore.getFloatOrNull
import java.util.function.Predicate

class CompoundTagFloatRangeCondition<T>(
    val props: Map<List<String>, Pair<Float, Float>> = emptyMap(),
    val matchAnyProp: Boolean = false,
    val getter: (t: T) -> CompoundTag,
) : Predicate<T> {
    override fun test(t: T): Boolean {
        val compoundTag = getter(t)
        return if (matchAnyProp) {
            props.entries.any { (k, v) -> internalTest(compoundTag, k, v.first, v.second) }
        } else {
            props.entries.all { (k, v) -> internalTest(compoundTag, k, v.first, v.second) }
        }
    }

    private fun internalTest(compoundTag: CompoundTag, keyChain: List<String>, min: Float, max: Float): Boolean {
        val pointer = if (keyChain.size > 1) keyChain.subList(0, keyChain.size - 1).fold(compoundTag) { acc, key ->
            val realKey = acc.allKeys.find { it.equals(key, true) } ?: return false
            acc.getCompoundOrNull(realKey) ?: return false
        } else compoundTag
        val target = pointer.getFloatOrNull(keyChain.last()) ?: return false
        return target in min..max
    }
}