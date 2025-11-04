package us.timinc.mc.cobblemon.timcore.influence

import com.cobblemon.mod.common.api.spawning.detail.SingleEntitySpawnAction
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import us.timinc.mc.cobblemon.timcore.TimCoreEvents
import us.timinc.mc.cobblemon.timcore.event.EntityDidSpawnEvent

class EntityDidSpawn : SpawningInfluence {
    override fun affectAction(action: SpawnAction<*>) {
        if (action !is SingleEntitySpawnAction<*>) return
        action.entity.subscribe { TimCoreEvents.ENTITY_DID_SPAWN.emit(EntityDidSpawnEvent(it, action.ctx)) }
    }
}