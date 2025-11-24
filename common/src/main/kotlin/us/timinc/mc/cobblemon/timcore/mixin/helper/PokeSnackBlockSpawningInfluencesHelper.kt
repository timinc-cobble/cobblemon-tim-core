package us.timinc.mc.cobblemon.timcore.mixin.helper

import com.cobblemon.mod.common.block.entity.PokeSnackBlockEntity
import us.timinc.mc.cobblemon.timcore.TimCore

object PokeSnackBlockSpawningInfluencesHelper {
    val alreadyInjected: MutableSet<PokeSnackBlockEntity> = mutableSetOf()

    fun handle(entity: PokeSnackBlockEntity) {
        cleanUp()
        if (alreadyInjected.contains(entity)) return
        entity.spawner.influences.addAll(TimCore.snackSpawnerInfluences)
        alreadyInjected.add(entity)
    }

    fun cleanUp() {
        alreadyInjected.removeIf { it.isRemoved }
    }
}