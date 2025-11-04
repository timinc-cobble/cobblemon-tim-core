package us.timinc.mc.cobblemon.timcore.event

import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import net.minecraft.world.entity.Entity

data class EntityDidSpawnEvent<T : Entity>(
    val entity: T,
    val ctx: SpawningContext
)