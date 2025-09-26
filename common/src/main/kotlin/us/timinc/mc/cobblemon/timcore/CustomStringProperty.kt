package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.properties.StringProperty

@Suppress("Unused", "MemberVisibilityCanBePrivate")
class CustomStringProperty(override val keys: Iterable<String>, val examples: Set<String>) :
    CustomPokemonPropertyType<StringProperty> {
    override val needsKey: Boolean = true
    override fun examples(): Collection<String> = examples

    override fun fromString(value: String?) = StringProperty(
        keys.first(), value ?: "", ::pokemonApplicator, ::pokemonMatcher
    )

    fun pokemonApplicator(pokemon: Pokemon, value: String) {
        pokemon.persistentData.putString(keys.first(), value)
    }

    fun entityApplicator(entity: PokemonEntity, value: String) {
        pokemonApplicator(entity.pokemon, value)
    }

    fun pokemonMatcher(pokemon: Pokemon, value: String): Boolean =
        pokemon.persistentData.contains(keys.first()) && (pokemon.persistentData.getString(keys.first()) == value)

    fun entityMatcher(entity: PokemonEntity, value: String): Boolean = pokemonMatcher(entity.pokemon, value)
}