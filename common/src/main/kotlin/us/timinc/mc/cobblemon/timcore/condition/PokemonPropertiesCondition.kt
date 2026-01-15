package us.timinc.mc.cobblemon.timcore.condition

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon
import java.util.function.Predicate

class PokemonPropertiesCondition(
    val props: String,
) : Predicate<Pokemon> {
    private val parsedProps by lazy {
        props.takeIf { it.isNotBlank() }?.let(PokemonProperties::parse)
    }

    override fun test(ctx: Pokemon): Boolean = parsedProps?.matches(ctx) ?: true
}