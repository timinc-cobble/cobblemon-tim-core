package us.timinc.mc.cobblemon.timcore.condition

import java.util.function.Predicate

class BooleanCondition<T>(
    val desired: Boolean?,
    val getter: (t: T) -> Boolean,
) : Predicate<T> {
    override fun test(t: T): Boolean = desired?.let { getter(t) == it } ?: true
}