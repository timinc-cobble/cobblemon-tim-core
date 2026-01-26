package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.mark.Mark
import com.cobblemon.mod.common.api.mark.Marks
import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.properties.BooleanProperty
import net.minecraft.resources.ResourceLocation

@Suppress("Unused", "MemberVisibilityCanBePrivate")
class CustomMarkBooleanProperty(override val keys: Iterable<String>, val markId: ResourceLocation) :
    CustomPokemonPropertyType<BooleanProperty> {
    constructor(key: String, markId: ResourceLocation) : this(setOf(key), markId)

    val mark: Mark?
        get() = Marks.getByIdentifier(markId)

    override val needsKey: Boolean = true

    override fun examples(): Collection<String> = setOf("yes", "no")

    override fun fromString(value: String?) = BooleanProperty(
        keys.first(), value == "yes", ::pokemonApplicator, ::entityApplicator, ::pokemonMatcher, ::entityMatcher
    )

    fun pokemonApplicator(pokemon: Pokemon, value: Boolean) {
        mark?.let { pokemon.exchangeMark(it, value) }
    }

    fun entityApplicator(entity: PokemonEntity, value: Boolean) {
        pokemonApplicator(entity.pokemon, value)
    }

    fun pokemonMatcher(pokemon: Pokemon, value: Boolean): Boolean =
        pokemon.marks.contains(mark) == value

    fun entityMatcher(entity: PokemonEntity, value: Boolean): Boolean = pokemonMatcher(entity.pokemon, value)
}