package us.timinc.mc.cobblemon.timcore.config

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon

interface OverridableOption<ValueType, ContextType> {
    val value: ValueType
    val overrides: Map<String, ValueType>

    fun getContextValue(context: ContextType): ValueType

    data class PokemonOption<ValueType>(
        override val value: ValueType,
        override val overrides: Map<String, ValueType> = emptyMap(),
    ) : OverridableOption<ValueType, Pokemon> {

        override fun getContextValue(context: Pokemon): ValueType {
            return overrides.entries.firstOrNull { (k) -> PokemonProperties.parse(k).matches(context) }?.value
                ?: value
        }
    }
}


