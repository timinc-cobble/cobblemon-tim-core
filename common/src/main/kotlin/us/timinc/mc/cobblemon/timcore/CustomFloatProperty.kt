package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.properties.FloatProperty
import org.apache.commons.lang3.math.NumberUtils.toFloat

@Suppress("Unused", "MemberVisibilityCanBePrivate")
open class CustomFloatProperty(override val keys: Iterable<String>) : CustomPokemonPropertyType<FloatProperty> {
    constructor(key: String) : this(setOf(key))

    override val needsKey: Boolean = true

    override fun examples(): Collection<String> = (0..100).map { (it / 100.0).toString() }

    override fun fromString(value: String?) = FloatProperty(
        keys.first(),
        toFloat(value),
        ::pokemonApplicator,
        ::entityApplicator,
        ::pokemonMatcher,
        ::entityMatcher
    )

    fun pokemonApplicator(pokemon: Pokemon, value: Float) {
        pokemon.persistentData.putFloat(keys.first(), value)
    }

    fun entityApplicator(entity: PokemonEntity, value: Float) {
        pokemonApplicator(entity.pokemon, value)
    }

    fun getValue(pokemon: Pokemon): Float? =
        if (!pokemon.persistentData.contains(keys.first())) null
        else pokemon.persistentData.getFloat(keys.first())

    fun pokemonMatcher(pokemon: Pokemon, value: Float): Boolean =
        pokemon.persistentData.contains(keys.first()) && (pokemon.persistentData.getFloat(keys.first()) == value)

    fun entityMatcher(entity: PokemonEntity, value: Float): Boolean = pokemonMatcher(entity.pokemon, value)
}