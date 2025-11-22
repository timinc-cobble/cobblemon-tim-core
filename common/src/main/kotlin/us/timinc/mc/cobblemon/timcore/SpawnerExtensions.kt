package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawner
import com.cobblemon.mod.common.api.spawning.spawner.Spawner

fun Spawner.getType(): String {
    if (this is PlayerSpawner) return TimCore.DataKeys.SpawnerTypes.PLAYER
    if (this.name.startsWith("poke_snack_spawner")) return TimCore.DataKeys.SpawnerTypes.SNACK
    if (this.name == "fishing") return TimCore.DataKeys.SpawnerTypes.FISHING
    return TimCore.DataKeys.SpawnerTypes.UNKNOWN
}