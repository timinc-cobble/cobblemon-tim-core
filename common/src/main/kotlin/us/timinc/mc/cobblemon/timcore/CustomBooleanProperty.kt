package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.properties.BooleanProperty

@Suppress("Unused", "MemberVisibilityCanBePrivate")
open class CustomBooleanProperty(override val keys: Iterable<String>) : CustomPokemonPropertyType<BooleanProperty> {
    constructor(key: String) : this(setOf(key))

    override val needsKey: Boolean = true

    override fun examples(): Collection<String> = setOf("yes", "no")

    override fun fromString(value: String?) = BooleanProperty(
        keys.first(), value == "yes", ::pokemonApplicator, ::entityApplicator, ::pokemonMatcher, ::entityMatcher
    )

    fun pokemonApplicator(pokemon: Pokemon, value: Boolean) {
        pokemon.persistentData.putBoolean(keys.first(), value)
    }

    fun entityApplicator(entity: PokemonEntity, value: Boolean) {
        pokemonApplicator(entity.pokemon, value)
    }

    fun pokemonMatcher(pokemon: Pokemon, value: Boolean): Boolean =
        (pokemon.persistentData.contains(keys.first()) && pokemon.persistentData.getBoolean(keys.first())) == value

    fun entityMatcher(entity: PokemonEntity, value: Boolean): Boolean = pokemonMatcher(entity.pokemon, value)
}