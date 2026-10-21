package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.api.spawning.detail.SingleEntitySpawnAction
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCoreEvents
import us.timinc.mc.cobblemon.timcore.event.EntityDidSpawnEvent
import us.timinc.mc.cobblemon.timcore.event.SpawningInfluenceEvents

object EntityDidSpawnHandler : AbstractHandler<SpawningInfluenceEvents.Action>() {
    override fun handle(evt: SpawningInfluenceEvents.Action) {
        val action = evt.action
        if (action !is SingleEntitySpawnAction<*>) return
        action.entity.subscribe {
            TimCoreEvents.ENTITY_DID_SPAWN.emit(
                EntityDidSpawnEvent(
                    it,
                    action
                )
            )
        }
    }
}
