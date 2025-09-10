package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class PokemonMatcher(
    val properties: String = "",
    val labels: List<String> = emptyList(),
    val anyLabel: Boolean = false,
    val persistentData: Map<String, String> = emptyMap(),
    val anyPersistentData: Boolean = false,
    val buckets: List<String> = emptyList(),
    val forms: List<String> = emptyList(),
    val matchOne: Boolean = false,
) {
    companion object {
        val CODEC: Codec<PokemonMatcher> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.optionalFieldOf("properties", "").forGetter { it.properties },
                Codec.STRING.listOf().optionalFieldOf("labels", emptyList()).forGetter { it.labels },
                Codec.BOOL.optionalFieldOf("anyLabel", false).forGetter { it.anyLabel },
                Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("persistentData", emptyMap())
                    .forGetter { it.persistentData },
                Codec.BOOL.optionalFieldOf("anyPersistentData", false).forGetter { it.anyPersistentData },
                Codec.STRING.listOf().optionalFieldOf("buckets", emptyList()).forGetter { it.buckets },
                Codec.STRING.listOf().optionalFieldOf("forms", emptyList()).forGetter { it.forms },
                Codec.BOOL.optionalFieldOf("matchOne", false).forGetter { it.matchOne }
            ).apply(instance, ::PokemonMatcher)
        }
    }

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
            if (forms.isNotEmpty()) add(::formsMatch)
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
        val spawnedInBucket = pokemon.getBucket() ?: return false
        return spawnedInBucket in bucketSet
    }

    private fun formsMatch(pokemon: Pokemon): Boolean {
        return forms.contains(pokemon.form.name)
    }
}
