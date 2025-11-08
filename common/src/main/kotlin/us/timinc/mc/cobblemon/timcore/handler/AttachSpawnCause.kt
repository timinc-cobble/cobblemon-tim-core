package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.api.spawning.fishing.FishingSpawner
import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawner
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCore
import us.timinc.mc.cobblemon.timcore.event.EntityDidSpawnEvent

object AttachSpawnCause : AbstractHandler<EntityDidSpawnEvent<PokemonEntity>>() {
    override fun handle(evt: EntityDidSpawnEvent<PokemonEntity>) {
        if (!TimCore.config.addSpawnCauseToData) return

        val spawner = evt.ctx.cause.spawner

        val spawnCause = when (spawner) {
            is PlayerSpawner -> TimCore.DataKeys.SpawnCauses.PLAYER_SPAWNER
            is FishingSpawner -> TimCore.DataKeys.SpawnCauses.FISHING
            else -> null
        } ?: return

        TimCore.CustomPokemonProperties.SPAWNED_VIA.entityApplicator(evt.entity, spawnCause.toString())
    }
}