package us.timinc.mc.cobblemon.timcore.neoforge

import net.minecraft.server.level.ServerLevel
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent
import us.timinc.mc.cobblemon.timcore.MOD_ID
import us.timinc.mc.cobblemon.timcore.TimCore
import us.timinc.mc.cobblemon.timcore.TimCoreEvents
import us.timinc.mc.cobblemon.timcore.event.EntityLoadEvent
import us.timinc.mc.cobblemon.timcore.event.EntityUnloadEvent

@Mod(MOD_ID)
object NeoForgeTimCore : AbstractNeoForgeMod(TimCore) {
    init {
        with(NeoForge.EVENT_BUS) {
            addListener(::onEntityLoad)
            addListener(::onEntityUnload)
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

    fun onEntityUnload(e: EntityLeaveLevelEvent) {
        TimCoreEvents.ENTITY_UNLOAD.post(
            EntityUnloadEvent(
                e.entity,
                e.level as? ServerLevel ?: return
            )
        )
    }
}