package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.properties.IntProperty

@Suppress("Unused", "MemberVisibilityCanBePrivate")
class CustomIntProperty(override val keys: Iterable<String>) : CustomPokemonPropertyType<IntProperty> {
    constructor(key: String) : this(setOf(key))

    override val needsKey: Boolean = true

    override fun examples(): Collection<String> = (0..100).map { (it / 100.0).toString() }

    override fun fromString(value: String?) = IntProperty(
        keys.first(),
        value?.toInt() ?: 0,
        ::pokemonApplicator,
        ::entityApplicator,
        ::pokemonMatcher,
        ::entityMatcher
    )

    fun pokemonApplicator(pokemon: Pokemon, value: Int) {
        pokemon.persistentData.putInt(keys.first(), value)
    }

    fun entityApplicator(entity: PokemonEntity, value: Int) {
        pokemonApplicator(entity.pokemon, value)
    }

    fun getValue(pokemon: Pokemon): Int? = pokemon.persistentData.getIntOrNull(keys.first())

    fun pokemonMatcher(pokemon: Pokemon, value: Int): Boolean = getValue(pokemon) == value

    fun entityMatcher(entity: PokemonEntity, value: Int): Boolean = pokemonMatcher(entity.pokemon, value)
}