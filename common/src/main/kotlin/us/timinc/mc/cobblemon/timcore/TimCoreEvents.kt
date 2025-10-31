package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.events.CobblemonEvents.BATTLE_FAINTED
import com.cobblemon.mod.common.api.events.CobblemonEvents.POKEMON_CAPTURED
import com.cobblemon.mod.common.api.events.CobblemonEvents.POKEMON_FAINTED
import com.cobblemon.mod.common.api.reactive.EventObservable
import com.cobblemon.mod.common.api.reactive.Observable.Companion.filter
import com.cobblemon.mod.common.util.isInBattle
import us.timinc.mc.cobblemon.timcore.event.CheckExpAllEvent
import us.timinc.mc.cobblemon.timcore.event.PokemonEntityTickedEvent

object TimCoreEvents {
    @JvmField
    val POKEMON_CAPTURED_IN_BATTLE = POKEMON_CAPTURED.pipe(
        filter { it.player.isInBattle() }
    )

    @JvmField
    val POKEMON_CAPTURED_OUT_OF_BATTLE = POKEMON_CAPTURED.pipe(
        filter { !it.player.isInBattle() }
    )

    @JvmField
    val BATTLE_FAINTED_PVW = BATTLE_FAINTED.pipe(
        filter { it.battle.isPvW }
    )

    @JvmField
    val BATTLE_FAINTED_PVW_WILD = BATTLE_FAINTED_PVW.pipe(
        filter { it.killed.effectedPokemon.isActuallyWild() }
    )

    @JvmField
    val BATTLE_FAINTED_PVW_PLAYER = BATTLE_FAINTED_PVW.pipe(
        filter { !it.killed.effectedPokemon.isActuallyWild() }
    )

    @JvmField
    val BATTLE_FAINTED_PVP = BATTLE_FAINTED.pipe(
        filter { it.battle.isPvP }
    )

    @JvmField
    val BATTLE_FAINTED_PVN = BATTLE_FAINTED.pipe(
        filter { it.battle.isPvN }
    )

    @JvmField
    val CHECK_EXP_ALL = EventObservable<CheckExpAllEvent.Check>()

    @JvmField
    val WILD_POKEMON_FAINTED = POKEMON_FAINTED.pipe(
        filter { it.pokemon.isActuallyWild() }
    )

    @JvmField
    val POKEMON_TICKED = EventObservable<PokemonEntityTickedEvent>()

    @JvmField
    val WILD_POKEMON_TICKED = POKEMON_TICKED
        .pipe(
            filter { it.entity.pokemon.isActuallyWild() }
        )
}