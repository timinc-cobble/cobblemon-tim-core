package us.timinc.mc.cobblemon.timcore.condition

import java.util.function.Predicate

class SetCondition<T, V>(
    val whitelist: Set<V> = emptySet(),
    val blacklist: Set<V> = emptySet(),
    val getter: (t: T) -> Set<V>,
    val matchAnyWhitelist: Boolean = false,
) : Predicate<T> {
    override fun test(t: T): Boolean = getter(t).let { list ->
        (if (matchAnyWhitelist) list.any { whitelist.contains(it) } else list.containsAll(whitelist))
                && !list.any { blacklist.contains(it) }
    }
}