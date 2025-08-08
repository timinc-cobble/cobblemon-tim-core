package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon
import us.timinc.mc.cobblemon.timcore.TimCore.debugger

data class PokemonMatcher(
    val properties: String = "",
    val labels: List<String> = emptyList(),
    val anyLabel: Boolean = false,
    val buckets: List<String> = emptyList(),
    val matchOne: Boolean = false,
) {
    fun matches(pokemon: Pokemon): Boolean =
        if (matchOne) propsMatch(pokemon) || labelsMatch(pokemon) || bucketMatches(pokemon) else propsMatch(pokemon) && labelsMatch(
            pokemon
        ) && bucketMatches(pokemon)

    private fun propsMatch(pokemon: Pokemon) = PokemonProperties.parse(properties).matches(pokemon)
    private fun labelsMatch(pokemon: Pokemon) =
        if (anyLabel) pokemon.form.labels.any(labels::contains) else pokemon.form.labels.containsAll(labels)

    private fun bucketMatches(pokemon: Pokemon): Boolean {
        val spawnedInBucket = pokemon.getBucket()
        if (spawnedInBucket == null) {
            val identifier = pokemon.getIdentifier()
            debugger.debug(
                "Could not determine spawn bucket of $identifier. Typical reasons include unnatural spawn, spawned before Tim Core installation, or data erased by another mod.",
                true
            )
            return false
        }
        return buckets.contains(spawnedInBucket)
    }
}