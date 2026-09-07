package us.timinc.mc.cobblemon.timcore.event

import com.cobblemon.mod.common.api.spawning.detail.SpawnAction
import com.cobblemon.mod.common.api.spawning.spawner.Spawner
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity

data class EntityDidSpawnEvent<T : Entity>(
    val entity: T,
    val action: SpawnAction<*>,
) {
    val cause: Entity?
        get() = action.spawnablePosition.cause.entity
    val playerCause: ServerPlayer?
        get() = action.spawnablePosition.cause.entity as? ServerPlayer
    val spawner: Spawner
        get() = action.spawnablePosition.spawner
    val bucket: String
        get() = action.bucket
}
