package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.item.ability.AbilityChanger
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.IVs
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.Species

abstract class PokemonRepresentation<T>(val pokemon: T) {
    class FromPokemon(pokemon: Pokemon) : PokemonRepresentation<Pokemon>(pokemon) {
        override val species: Species
            get() = pokemon.species
        override val form: FormData
            get() = pokemon.form
        override val abilityName: String
            get() = pokemon.ability.name
        override val hasHiddenAbility: Boolean
            get() = AbilityChanger.HIDDEN_ABILITY.queryPossible(pokemon).isNotEmpty()
        override val shiny: Boolean
            get() = pokemon.shiny

        override fun giveHiddenAbility() {
            AbilityChanger.HIDDEN_ABILITY.performChange(pokemon)
        }

        override fun makePerfectIvs(count: Int): Set<Stat> {
            val imperfects = pokemon.ivs.filter { (_, value) -> value != IVs.MAX_VALUE }.shuffled().take(count)
            for ((stat) in imperfects) {
                pokemon.setIV(stat, IVs.MAX_VALUE)
            }
            return imperfects.map { (stat) -> stat }.toSet()
        }

        override fun makeShiny() {
            pokemon.shiny = true
        }

        override fun getPokemon(): Pokemon = pokemon
    }

    class FromProperties(pokemon: PokemonProperties) : PokemonRepresentation<PokemonProperties>(pokemon) {
        override val species: Species?
            get() = pokemon.species?.let { PokemonSpecies.getByName(it) }
        override val form: FormData?
            get() = species?.let { innerSpecies ->
                pokemon.form?.let { innerForm ->
                    innerSpecies.forms.firstOrNull { innerSpeciesForm ->
                        innerSpeciesForm.formOnlyShowdownId().equals(innerForm, false)
                    }
                } ?: innerSpecies.forms.firstOrNull { pokemon.aspects.containsAll(it.aspects) }
                ?: innerSpecies.standardForm
            }
        override val abilityName: String?
            get() = pokemon.ability
        override val hasHiddenAbility: Boolean
            get() = form?.abilities?.mapping?.get(Priority.LOW)?.isNotEmpty() ?: false
        override val shiny: Boolean
            get() = pokemon.shiny ?: false

        override fun giveHiddenAbility() {
            val hiddenAbility = form?.abilities?.mapping?.get(Priority.LOW)?.random()?.template?.name ?: return
            pokemon.ability = hiddenAbility
        }

        override fun makePerfectIvs(count: Int): Set<Stat> {
            val ivs = pokemon.ivs
            if (ivs == null || ivs.toList().isEmpty()) {
                val freshIvs = IVs.createRandomIVs(count)
                pokemon.ivs = freshIvs
                return freshIvs.filter { (_, value) -> value == IVs.MAX_VALUE }.take(count).map { (stat) -> stat }
                    .toSet()
            }

            val imperfects = ivs.filter { (_, value) -> value != IVs.MAX_VALUE }.shuffled().take(count)
            for ((stat) in imperfects) {
                ivs[stat] = IVs.MAX_VALUE
            }
            return imperfects.map { (stat) -> stat }.toSet()
        }

        override fun makeShiny() {
            pokemon.shiny = true
        }

        override fun getPokemon(): Pokemon = pokemon.create()
    }

    class FromEntity(pokemon: PokemonEntity) : PokemonRepresentation<PokemonEntity>(pokemon) {
        override val species: Species
            get() = pokemon.pokemon.species
        override val form: FormData
            get() = pokemon.pokemon.form
        override val abilityName: String
            get() = pokemon.pokemon.ability.name
        override val hasHiddenAbility: Boolean
            get() = AbilityChanger.HIDDEN_ABILITY.queryPossible(pokemon.pokemon).isNotEmpty()
        override val shiny: Boolean
            get() = pokemon.pokemon.shiny

        override fun giveHiddenAbility() {
            AbilityChanger.HIDDEN_ABILITY.performChange(pokemon.pokemon)
        }

        override fun makePerfectIvs(count: Int): Set<Stat> {
            val imperfects = pokemon.pokemon.ivs.filter { (_, value) -> value != IVs.MAX_VALUE }.shuffled().take(count)
            for ((stat) in imperfects) {
                pokemon.pokemon.setIV(stat, IVs.MAX_VALUE)
            }
            return imperfects.map { (stat) -> stat }.toSet()
        }

        override fun makeShiny() {
            pokemon.pokemon.shiny = true
        }

        override fun getPokemon(): Pokemon = pokemon.pokemon
    }

    abstract val species: Species?
    abstract val form: FormData?
    abstract val abilityName: String?
    abstract val hasHiddenAbility: Boolean
    abstract val shiny: Boolean

    abstract fun giveHiddenAbility()
    abstract fun makePerfectIvs(count: Int): Set<Stat>
    abstract fun makeShiny()
    abstract fun getPokemon(): Pokemon
}
