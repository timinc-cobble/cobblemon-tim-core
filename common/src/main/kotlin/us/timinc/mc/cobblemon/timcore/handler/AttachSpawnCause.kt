package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCore
import us.timinc.mc.cobblemon.timcore.event.EntityDidSpawnEvent
import us.timinc.mc.cobblemon.timcore.getType

object AttachSpawnCause : AbstractHandler<EntityDidSpawnEvent<PokemonEntity>>() {
    override fun handle(evt: EntityDidSpawnEvent<PokemonEntity>) {
        if (!TimCore.config.addSpawnCauseToData) return

        val spawnedOn = evt.playerCause?.stringUUID
        val spawnedVia = evt.spawner.getType()

        TimCore.CustomPokemonProperties.SPAWNED_VIA.entityApplicator(evt.entity, spawnedVia)
        spawnedOn?.let { TimCore.CustomPokemonProperties.SPAWNED_ON.entityApplicator(evt.entity, it) }
    }
}
