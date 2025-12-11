package us.timinc.mc.cobblemon.timcore.mixin.helper

import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import com.cobblemon.mod.common.block.entity.PokeSnackBlockEntity

object PokeSnackBlockSpawningInfluencesHelper {
    val alreadyInjected: MutableSet<PokeSnackBlockEntity> = mutableSetOf()
    val snackSpawnerInfluences: MutableSet<SpawningInfluence> = mutableSetOf()

    fun handle(entity: PokeSnackBlockEntity) {
        cleanUp()
        if (alreadyInjected.contains(entity)) return
        entity.spawner.influences.addAll(snackSpawnerInfluences)
        alreadyInjected.add(entity)
    }

    fun cleanUp() {
        alreadyInjected.removeIf { it.isRemoved }
    }
}