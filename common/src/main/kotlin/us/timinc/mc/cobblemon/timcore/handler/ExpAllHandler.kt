package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemon.mod.common.api.tags.CobblemonItemTags
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCore.config
import us.timinc.mc.cobblemon.timcore.TimCore.debugger
import us.timinc.mc.cobblemon.timcore.getIdentifier
import us.timinc.mc.cobblemon.timcore.hasExpAll

object ExpAllHandler : AbstractHandler<BattleVictoryEvent>() {
    override fun handle(evt: BattleVictoryEvent) {
        if (!config.enableExpAll) return

        val caseDebugger = debugger.getCaseDebugger()
        caseDebugger.debug("Reviewing post-battle for ExpAll logic.")

        for (winner in evt.winners) {
            for (winningPokemon in winner.pokemonList) {
                val winningPokemonIdentifier = winningPokemon.effectedPokemon.getIdentifier()
                caseDebugger.debug("Reviewing winner $winningPokemonIdentifier...")
                val owner = winningPokemon.originalPokemon.getOwnerPlayer()
                if (owner == null) {
                    caseDebugger.debug("Not player-owned, skipping.")
                    continue
                }
                if (!owner.hasExpAll()) {
                    caseDebugger.debug("Player ${owner.name} does not have an ExpAll, skipping.")
                    continue
                }
                for (loser in evt.losers) {
                    for (losingPokemon in loser.pokemonList) {
                        val losingPokemonIdentifier = losingPokemon.effectedPokemon.getIdentifier()
                        caseDebugger.debug("Reviewing loser $losingPokemonIdentifier...")
                        if (winningPokemon.facedOpponents.contains(losingPokemon)) {
                            caseDebugger.debug("Faced $losingPokemonIdentifier, skipping as exp already given by Cobblemon.")
                            continue
                        }
                        if (winningPokemon.effectedPokemon.heldItem().`is`(CobblemonItemTags.EXPERIENCE_SHARE)) {
                            caseDebugger.debug("Holding ExpShare, skipping as exp already given by Cobblemon.")
                            continue
                        }
                        val experience = Cobblemon.experienceCalculator.calculate(winningPokemon, losingPokemon, config.expAllMultiplier.toDouble())
                        if (experience <= 0) {
                            caseDebugger.debug("Calculated experience less than or equal to 0, skipping.")
                            continue
                        }

                        caseDebugger.debug("Awarding $experience experience.")
                        winningPokemon.actor.awardExperience(winningPokemon, experience)
                    }
                }
            }
        }
    }
}