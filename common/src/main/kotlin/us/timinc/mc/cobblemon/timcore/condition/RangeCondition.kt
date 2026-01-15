package us.timinc.mc.cobblemon.timcore.condition

import java.util.function.Predicate

class RangeCondition<T, V : Comparable<V>>(
    val min: V,
    val max: V,
    val getter: (t: T) -> V,
) : Predicate<T> {
    override fun test(t: T): Boolean = getter(t) in min..max
}