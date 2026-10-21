package us.timinc.mc.cobblemon.timcore.influence

import com.cobblemon.mod.common.api.spawning.detail.SpawnAction
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.cobblemon.mod.common.api.spawning.position.calculators.SpawnablePositionCalculator
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import us.timinc.mc.cobblemon.timcore.TimCoreEvents
import us.timinc.mc.cobblemon.timcore.event.SpawningInfluenceEvents

class EventSpawningInfluence : SpawningInfluence {
    override fun affectSpawnablePosition(spawnablePosition: SpawnablePosition) {
        TimCoreEvents.SPAWNING_POSITION.emit(SpawningInfluenceEvents.Position(spawnablePosition))
    }

    override fun affectSpawnable(detail: SpawnDetail, spawnablePosition: SpawnablePosition): Boolean {
        val event = SpawningInfluenceEvents.Spawnable(detail, spawnablePosition)
        TimCoreEvents.SPAWNING_SPAWNABLE.emit(event)
        return event.allowed
    }

    override fun affectWeight(detail: SpawnDetail, spawnablePosition: SpawnablePosition, weight: Float): Float {
        val event = SpawningInfluenceEvents.Weight(detail, spawnablePosition, weight)
        TimCoreEvents.SPAWNING_WEIGHT.emit(event)
        return event.weight
    }

    override fun affectAction(action: SpawnAction<*>) {
        TimCoreEvents.SPAWNING_ACTION.emit(SpawningInfluenceEvents.Action(action))
    }

    override fun affectSpawn(action: SpawnAction<*>, entity: Entity) {
        TimCoreEvents.SPAWNING_SPAWN.emit(SpawningInfluenceEvents.Spawn(action, entity))
    }

    override fun affectBucketWeights(bucketWeights: MutableMap<String, Float>) {
        TimCoreEvents.SPAWNING_BUCKET_WEIGHTS.emit(SpawningInfluenceEvents.BucketWeights(bucketWeights))
    }

    override fun isAllowedPosition(
        world: ServerLevel,
        pos: BlockPos,
        spawnablePositionCalculator: SpawnablePositionCalculator<*, *>,
    ): Boolean {
        val event = SpawningInfluenceEvents.AllowedPosition(world, pos, spawnablePositionCalculator)
        TimCoreEvents.SPAWNING_ALLOWED_POSITION.emit(event)
        return event.allowed
    }

    override fun injectSpawns(bucket: String, spawnablePosition: SpawnablePosition): List<SpawnDetail>? {
        val event = SpawningInfluenceEvents.InjectSpawns(bucket, spawnablePosition)
        TimCoreEvents.SPAWNING_INJECT_SPAWNS.emit(event)
        return event.spawns.takeIf { it.isNotEmpty() }
    }
}
