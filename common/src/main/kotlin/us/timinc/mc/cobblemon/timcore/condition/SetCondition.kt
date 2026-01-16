package us.timinc.mc.cobblemon.timcore.condition

import java.util.function.Predicate

class SetCondition<T, V>(
    val whitelist: Set<V> = emptySet(),
    val blacklist: Set<V> = emptySet(),
    val getter: (t: T) -> Set<V>,
    val matchAnyWhitelist: Boolean = false,
) : Predicate<T> {
    override fun test(t: T): Boolean = getter(t).let { list ->
        val passedWhitelist = whitelist.isEmpty()
                || (matchAnyWhitelist && whitelist.any { list.contains(it) })
                || (!matchAnyWhitelist && whitelist.all { list.contains(it) })
        val passedBlacklist = blacklist.isEmpty()
                || blacklist.none { list.contains(it) }
        return passedWhitelist && passedBlacklist
    }
}