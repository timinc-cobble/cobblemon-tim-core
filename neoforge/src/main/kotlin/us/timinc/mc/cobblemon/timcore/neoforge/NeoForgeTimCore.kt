package us.timinc.mc.cobblemon.timcore.neoforge

import net.minecraft.server.level.ServerLevel
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import us.timinc.mc.cobblemon.timcore.MOD_ID
import us.timinc.mc.cobblemon.timcore.TimCore
import us.timinc.mc.cobblemon.timcore.TimCoreEvents
import us.timinc.mc.cobblemon.timcore.event.EntityLoadEvent

@Mod(MOD_ID)
object NeoForgeTimCore : AbstractNeoForgeMod(TimCore) {
    init {
        with(NeoForge.EVENT_BUS) {
            addListener(::onEntityLoad)
        }
    }

    fun onEntityLoad(e: EntityJoinLevelEvent) {
        TimCoreEvents.ENTITY_LOAD.post(
            EntityLoadEvent(
                e.entity,
                e.level as? ServerLevel ?: return
            )
        )
    }
}