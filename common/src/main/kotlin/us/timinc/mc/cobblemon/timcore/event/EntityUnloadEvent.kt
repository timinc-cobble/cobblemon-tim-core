package us.timinc.mc.cobblemon.timcore.event

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity

data class EntityUnloadEvent<T : Entity>(
    val entity: T,
    val world: ServerLevel,
)