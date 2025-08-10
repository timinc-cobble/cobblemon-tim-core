package us.timinc.mc.cobblemon.timcore.config

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon

abstract class OverridableOption<ValueType, ContextType>(
    open val value: ValueType,
    open val overrides: Map<String, ValueType> = emptyMap(),
) {
    data class PokemonOption<ValueType>(override val value: ValueType, override val overrides: Map<String, ValueType>) :
        OverridableOption<ValueType, Pokemon>(value, overrides) {
        override fun getValue(context: Pokemon): ValueType =
            overrides.entries.find { (k) -> PokemonProperties.parse(k).matches(context) }?.value ?: value
    }

    abstract fun getValue(context: ContextType): ValueType
}
