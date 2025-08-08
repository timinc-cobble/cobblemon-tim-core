package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon
import us.timinc.mc.cobblemon.timcore.TimCore.debugger

data class PokemonMatcher(
    val properties: String = "",
    val labels: List<String> = emptyList(),
    val anyLabel: Boolean = false,
    val persistentData: Map<String, String> = emptyMap(),
    val anyPersistentData: Boolean = false,
    val buckets: List<String> = emptyList(),
    val matchOne: Boolean = false,
) {
    @delegate:Transient
    private val parsedProps by lazy {
        properties.takeIf { it.isNotBlank() }?.let(PokemonProperties::parse)
    }

    @Transient
    private val labelSet = labels.toSet()

    @Transient
    private val bucketSet = buckets.toSet()

    fun matches(pokemon: Pokemon): Boolean {
        val predicates = buildList<(Pokemon) -> Boolean> {
            if (parsedProps != null) add { p -> parsedProps!!.matches(p) }
            if (labelSet.isNotEmpty()) add(::labelsMatch)
            if (persistentData.isNotEmpty()) add(::persistentDataMatch)
            if (bucketSet.isNotEmpty()) add(::bucketMatch)
        }

        if (predicates.isEmpty()) return true

        return if (matchOne) predicates.any { it(pokemon) } else predicates.all { it(pokemon) }
    }

    private fun labelsMatch(pokemon: Pokemon): Boolean {
        val pokeLabels = pokemon.form.labels
        return if (anyLabel) pokeLabels.any(labelSet::contains) else pokeLabels.containsAll(labelSet)
    }

    private fun persistentDataMatch(pokemon: Pokemon): Boolean {
        val pd = pokemon.persistentData
        return if (anyPersistentData) {
            persistentData.entries.any { (k, v) -> pd.getOrNull(k)?.toString() == v }
        } else {
            persistentData.entries.all { (k, v) -> pd.getOrNull(k)?.toString() == v }
        }
    }

    private fun bucketMatch(pokemon: Pokemon): Boolean {
        val spawnedInBucket = pokemon.getBucket()
        if (spawnedInBucket == null) {
            debugger.debug(
                "Could not determine spawn bucket of ${pokemon.getIdentifier()}. " +
                        "Common reasons: unnatural spawn, pre-TimCore spawn, or data erased by another mod.",
                true
            )
            return false
        }
        return spawnedInBucket in bucketSet
    }
}
