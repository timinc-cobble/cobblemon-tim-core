package us.timinc.mc.cobblemon.timcore.pokemonpropertyextractor

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty
import com.cobblemon.mod.common.pokemon.Pokemon
import us.timinc.mc.cobblemon.timcore.data.CustomPropertyExtractorWhitelistManager

class CustomPropertiesExtractor(
    val context: String,
) : PokemonPropertyExtractor {
    override fun invoke(
        pokemon: Pokemon,
        properties: PokemonProperties,
    ) {
        pokemon.features.forEach { feature ->
            if (!CustomPropertyExtractorWhitelistManager.isAllowed(feature.name, context)) return@forEach
            properties.customProperties.add(feature as? CustomPokemonProperty ?: return@forEach)
        }
    }
}