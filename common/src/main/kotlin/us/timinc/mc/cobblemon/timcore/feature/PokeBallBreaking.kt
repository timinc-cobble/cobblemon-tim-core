package us.timinc.mc.cobblemon.timcore.feature

import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import us.timinc.mc.cobblemon.timcore.TimCore
import us.timinc.mc.cobblemon.timcore.TimCoreEvents
import us.timinc.mc.cobblemon.timcore.event.PokeBallBreakEvent
import kotlin.random.Random.Default.nextFloat

object PokeBallBreaking {
    fun shouldBreak(pokeBallEntity: EmptyPokeBallEntity, reason: String, hitBlockPos: BlockPos? = null): Boolean {
        var chance = TimCore.config.ballBreakChance
        val level = pokeBallEntity.level() as? ServerLevel ?: return false
        val pokeBall = pokeBallEntity.pokeBall
        val pokeBallOwner = pokeBallEntity.owner
        val targetPokemon = pokeBallEntity.capturingPokemon

        val genericEvent = PokeBallBreakEvent.Generic(
            pokeBall,
            reason,
            level,
            pokeBallOwner,
            targetPokemon,
            hitBlockPos,
        )

        val debugger = TimCore.debugger.getCaseDebugger()

        debugger.debug("Calculating whether or not to break ball...")

        if (targetPokemon?.owner == pokeBallOwner && TimCore.config.dontBreakBallOnHittingOwnPokemon) {
            debugger.debug("Hit own Pokemon and config says don't break for that.")
            return false
        }

        debugger.debug("Event: $genericEvent")
        debugger.debug("Chance per config: $chance")

        TimCoreEvents.POKE_BALL_BREAK_CHANCE.post(
            genericEvent.toChance(chance)
        ) {
            debugger.debug("Chance per event listener evaluation: ${it.chance}")
            chance = it.chance
        }

        val roll = nextFloat()
        debugger.debug("Rolled $roll")
        if (roll < chance) {
            debugger.debug("Successfully rolled for a break.")

            TimCoreEvents.POKE_BALL_BREAK_PRE.postThen(
                genericEvent.toPre(),
                ifCanceled = {
                    debugger.debug("An event listener cancelled the breaking.")
                },
                ifSucceeded = {
                    debugger.debug("Breaking ball!")
                    TimCoreEvents.POKE_BALL_BREAK_POST.post(
                        genericEvent.toPost(),
                    )
                    return true
                }
            )
        }
        return false
    }
}