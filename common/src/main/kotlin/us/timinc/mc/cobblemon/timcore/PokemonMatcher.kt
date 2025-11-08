package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.IVs
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.splitMap
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class PokemonMatcher(
    var properties: String = "",
    var labels: List<String> = emptyList(),
    var anyLabel: Boolean = false,
    var persistentData: Map<String, String> = emptyMap(),
    var anyPersistentData: Boolean = false,
    var buckets: List<String> = emptyList(),
    var forms: List<String> = emptyList(),
    var maxIVs: Int = -1,
    var matchOne: Boolean = false,
) {
    companion object {
        @Deprecated("Favor the string Codec instead. This will be removed in the future.")
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
                Codec.INT.optionalFieldOf("maxIVs", -1).forGetter { it.maxIVs },
                Codec.BOOL.optionalFieldOf("matchOne", false).forGetter { it.matchOne }
            ).apply(instance, ::PokemonMatcher)
        }

        val STRING_CODEC: Codec<PokemonMatcher> = Codec.STRING.xmap(
            { parse(it) },
            { it.asString() }
        )

        fun parse(
            string: String,
            delimiter: String = " ",
            assigner: String = "=",
            innerDelimiter: String = ",",
        ): PokemonMatcher {
            val matcher = PokemonMatcher()
            matcher.properties = string
            val keyPairs = string.splitMap(delimiter, assigner)
            matcher.labels = parseString(keyPairs, listOf("labels"))?.split(innerDelimiter) ?: listOf()
            matcher.anyLabel = parseBooleanProperty(keyPairs, listOf("any_label")) ?: false
            matcher.buckets = parseString(keyPairs, listOf("buckets"))?.split(innerDelimiter) ?: listOf()
            matcher.forms = parseString(keyPairs, listOf("forms"))?.split(innerDelimiter) ?: listOf()
            matcher.matchOne = parseBooleanProperty(keyPairs, listOf("match_one")) ?: false
            matcher.persistentData = parseString(keyPairs, listOf("persistent_data"))?.let {
                it.splitMap(innerDelimiter, assigner).fold(mutableMapOf()) { acc, (k, v) ->
                    acc[k] = v ?: ""
                    acc
                }
            } ?: emptyMap()
            matcher.anyPersistentData = parseBooleanProperty(keyPairs, listOf("any_persistent_data")) ?: false
            matcher.maxIVs = parseInt(keyPairs, listOf("maxIVs")) ?: -1
            return matcher
        }

        private fun getMatchedKeyPair(
            keyPairs: MutableList<Pair<String, String?>>,
            labels: Iterable<String>,
        ): Pair<String, String?>? {
            return keyPairs.findLast { it.first in labels }
        }

        private fun parseString(keyPairs: MutableList<Pair<String, String?>>, labels: Iterable<String>): String? {
            val matchingKeyPair = getMatchedKeyPair(keyPairs, labels) ?: return null
            val value = matchingKeyPair.second
            return if (value.isNullOrBlank()) {
                null
            } else {
                value
            }
        }

        private fun parseInt(keyPairs: MutableList<Pair<String, String?>>, labels: Iterable<String>): Int? {
            val stringValue = parseString(keyPairs, labels) ?: return null
            try {
                return stringValue.toInt()
            } catch (e: NumberFormatException) {
                TimCore.debugger.debug("Attempted to use non-int value of $stringValue for maxIVs.")
                return -1
            }
        }

        private fun parseBooleanProperty(
            keyPairs: MutableList<Pair<String, String?>>,
            labels: Iterable<String>,
        ): Boolean? {
            val matchingKeyPair = getMatchedKeyPair(keyPairs, labels) ?: return null
            keyPairs.remove(matchingKeyPair)
            return when (matchingKeyPair.second?.lowercase()) {
                null -> true
                "true", "yes" -> true
                "false", "no" -> false
                else -> null
            }
        }
    }

    @delegate:Transient
    private val parsedProps by lazy {
        properties.takeIf { it.isNotBlank() }?.let(PokemonProperties::parse)
    }

    fun matches(pokemon: Pokemon): Boolean {
        val predicates = buildList<(Pokemon) -> Boolean> {
            if (parsedProps != null) add { p -> parsedProps!!.matches(p) }
            if (labels.isNotEmpty()) add(::labelsMatch)
            if (persistentData.isNotEmpty()) add(::persistentDataMatch)
            if (buckets.isNotEmpty()) add(::bucketMatch)
            if (forms.isNotEmpty()) add(::formsMatch)
            if (maxIVs != -1) add(::maxIVsMatch)
        }

        if (predicates.isEmpty()) return true

        return if (matchOne) predicates.any { it(pokemon) } else predicates.all { it(pokemon) }
    }

    private fun maxIVsMatch(pokemon: Pokemon): Boolean =
        pokemon.ivs.count { (_, int) -> int == IVs.MAX_VALUE } >= maxIVs

    private fun labelsMatch(pokemon: Pokemon): Boolean {
        val pokeLabels = pokemon.form.labels
        return if (anyLabel) pokeLabels.any(labels::contains) else pokeLabels.containsAll(labels)
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
        return spawnedInBucket in buckets
    }

    private fun formsMatch(pokemon: Pokemon): Boolean {
        return forms.contains(pokemon.form.name)
    }

    fun asString(separator: String = " "): String {
        val stringed = mutableListOf<String>()

        if (properties != "") stringed.add(properties)
        if (labels.isNotEmpty()) stringed.add("labels=${labels.joinToString(",")}")
        if (anyLabel) stringed.add("any_label")
        if (buckets.isNotEmpty()) stringed.add("buckets=${buckets.joinToString(",")}")
        if (forms.isNotEmpty()) stringed.add("forms=${forms.joinToString(",")}")
        if (matchOne) stringed.add("match_one")
        if (persistentData.isNotEmpty()) stringed.add("persistent_data=${persistentData.entries.joinToString(",") { (k, v) -> "$k=$v" }}")
        if (anyPersistentData) stringed.add("any_persistent_data")
        if (maxIVs != -1) stringed.add("maxIVs=$maxIVs")

        return stringed.joinToString(separator)
    }
}
