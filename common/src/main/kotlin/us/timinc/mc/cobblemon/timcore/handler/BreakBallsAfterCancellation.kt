package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.api.events.pokeball.ThrownPokeballHitEvent
import net.minecraft.world.entity.Entity
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCore.DataKeys
import us.timinc.mc.cobblemon.timcore.feature.PokeBallBreaking

object BreakBallsAfterCancellation : AbstractHandler<ThrownPokeballHitEvent>() {
    override fun handle(evt: ThrownPokeballHitEvent) {
        if (evt.isCanceled && PokeBallBreaking.shouldBreak(
                evt.pokeBall,
                DataKeys.PokeBallBreakReasons.EVT_CANCELLED
            )
        ) evt.pokeBall.remove(Entity.RemovalReason.KILLED)
    }
}