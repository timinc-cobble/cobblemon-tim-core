package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.pokemon.Pokemon

abstract class LimitedList<X, Y> {
    class Generic<T> : LimitedList<T, T>() {
        override fun isInList(entry: T, list: Set<T>): Boolean = entry in list
    }

    object PokemonMatcherList : LimitedList<Pokemon, PokemonMatcher>() {
        override fun isInList(entry: Pokemon, list: Set<PokemonMatcher>): Boolean = list.any { it.matches(entry) }
    }

    fun matchesList(entry: X, whitelist: Set<Y>, blacklist: Set<Y>): Boolean = when {
        whitelist.isNotEmpty() && blacklist.isNotEmpty() -> isInList(entry, whitelist) && !isInList(entry, blacklist)

        whitelist.isNotEmpty() -> isInList(entry, whitelist)
        blacklist.isNotEmpty() -> !isInList(entry, blacklist)
        else -> true
    }

    abstract fun isInList(entry: X, list: Set<Y>): Boolean
}
