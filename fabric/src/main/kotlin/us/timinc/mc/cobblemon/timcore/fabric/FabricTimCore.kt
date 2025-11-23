package us.timinc.mc.cobblemon.timcore.fabric

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import us.timinc.mc.cobblemon.timcore.TimCore
import us.timinc.mc.cobblemon.timcore.TimCoreEvents
import us.timinc.mc.cobblemon.timcore.event.EntityLoadEvent

object FabricTimCore : AbstractFabricMod(TimCore) {
    override fun onInitialize() {
        ServerEntityEvents.ENTITY_LOAD.register { entity, world ->
            TimCoreEvents.ENTITY_LOAD.post(
                EntityLoadEvent(
                    entity,
                    world
                )
            )
        }
    }
}