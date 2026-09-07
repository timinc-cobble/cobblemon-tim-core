package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemon.mod.common.api.tags.CobblemonItemTags
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCore.config
import us.timinc.mc.cobblemon.timcore.TimCore.debugger
import us.timinc.mc.cobblemon.timcore.getIdentifier
import us.timinc.mc.cobblemon.timcore.hasExpAllFor

object ExpAllHandler : AbstractHandler<BattleVictoryEvent>() {
    override fun handle(evt: BattleVictoryEvent) {
        if (!config.enableExpAll) return

        val caseDebugger = debugger.getCaseDebugger()
        caseDebugger.debug("Reviewing post-battle for ExpAll logic.")

        val awardToFainted = Cobblemon.config.awardExperienceToFaintedPokemon
        val awardOnLoss = Cobblemon.config.awardExperienceOnBattleLoss

        for (defeatedActor in evt.battle.actors) {
            val defeatedPokemon = defeatedActor.pokemonList.filter { it.health <= 0 }
            if (defeatedPokemon.isEmpty()) continue

            val teamWiped = defeatedActor.pokemonList.all { it.health <= 0 }
            if (!teamWiped && !awardOnLoss) {
                caseDebugger.debug("Skipping a non-wiped team because experience on battle loss is disabled.")
                continue
            }

            for (recipientActor in defeatedActor.getSide().getOppositeSide().actors) {
                for (recipient in recipientActor.pokemonList) {
                    val recipientIdentifier = recipient.effectedPokemon.getIdentifier()
                    caseDebugger.debug("Reviewing recipient $recipientIdentifier...")

                    val owner = recipient.originalPokemon.getOwnerPlayer()
                    if (owner == null) {
                        caseDebugger.debug("Not player-owned, skipping.")
                        continue
                    }
                    if (!owner.hasExpAllFor(recipient.effectedPokemon)) {
                        caseDebugger.debug("Player ${owner.name} does not have an ExpAll, skipping.")
                        continue
                    }
                    if (recipient.effectedPokemon.heldItem().`is`(CobblemonItemTags.EXPERIENCE_SHARE)) {
                        caseDebugger.debug("Holding ExpShare, skipping as exp is already given by Cobblemon.")
                        continue
                    }
                    if (recipient.health <= 0 && !awardToFainted) {
                        caseDebugger.debug("Fainted and experience for fainted Pokémon is disabled, skipping.")
                        continue
                    }

                    for (faintedOpponent in defeatedPokemon) {
                        val faintedOpponentIdentifier = faintedOpponent.effectedPokemon.getIdentifier()
                        caseDebugger.debug("Reviewing fainted opponent $faintedOpponentIdentifier...")

                        if (awardToFainted) {
                            val opponentFaintedAt = faintedOpponent.faintedAt ?: continue
                            val recipientFaintedAt = recipient.faintedAt
                            if (recipientFaintedAt != null && recipientFaintedAt <= opponentFaintedAt) {
                                caseDebugger.debug("Recipient fainted before $faintedOpponentIdentifier, skipping.")
                                continue
                            }
                        }
                        if (recipient.facedOpponents.contains(faintedOpponent)) {
                            caseDebugger.debug("Faced $faintedOpponentIdentifier, skipping as exp is already given by Cobblemon.")
                            continue
                        }

                        val experience = Cobblemon.experienceCalculator.calculate(
                            recipient,
                            faintedOpponent,
                            config.expAllMultiplier.toDouble()
                        )
                        if (experience <= 0) {
                            caseDebugger.debug("Calculated experience less than or equal to 0, skipping.")
                            continue
                        }

                        caseDebugger.debug("Awarding $experience experience.")
                        recipient.actor.awardExperience(recipient, experience)
                    }
                }
            }
        }
    }
}
