package us.timinc.mc.cobblemon.timcore.event

import com.cobblemon.mod.common.api.spawning.detail.SpawnAction
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import com.cobblemon.mod.common.api.spawning.position.calculators.SpawnablePositionCalculator
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity

object SpawningInfluenceEvents {
    data class Position(val position: SpawnablePosition)

    data class Spawnable(val detail: SpawnDetail, val position: SpawnablePosition, var allowed: Boolean = true)

    data class Weight(val detail: SpawnDetail, val position: SpawnablePosition, var weight: Float)

    data class Action(val action: SpawnAction<*>)

    data class Spawn(val action: SpawnAction<*>, val entity: Entity) {
        val position: SpawnablePosition get() = action.spawnablePosition
    }

    data class BucketWeights(val weights: MutableMap<String, Float>)

    data class AllowedPosition(
        val world: ServerLevel,
        val pos: BlockPos,
        val calculator: SpawnablePositionCalculator<*, *>,
        var allowed: Boolean = true,
    )

    data class InjectSpawns(
        val bucket: String,
        val position: SpawnablePosition,
        val spawns: MutableList<SpawnDetail> = mutableListOf(),
    )
}
