package us.timinc.mc.cobblemon.timcore.mixin.helper

import com.cobblemon.mod.common.block.entity.PokeSnackBlockEntity
import us.timinc.mc.cobblemon.timcore.TimCore

object PokeSnackBlockSpawningInfluencesHelper {
    fun handle(entity: PokeSnackBlockEntity) {
        entity.spawner.influences.addAll(TimCore.snackSpawnerInfluences)
    }
}