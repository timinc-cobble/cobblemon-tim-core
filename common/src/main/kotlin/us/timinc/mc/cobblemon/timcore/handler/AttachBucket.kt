package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.api.events.entity.SpawnEvent
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCore.DataKeys.SPAWNED_IN_BUCKET
import us.timinc.mc.cobblemon.timcore.TimCore.config

object AttachBucket: AbstractHandler<SpawnEvent<PokemonEntity>>() {
    override fun handle(evt: SpawnEvent<PokemonEntity>) {
        if (!config.addBucketToData) return
        evt.entity.pokemon.persistentData.putString(SPAWNED_IN_BUCKET, evt.ctx.cause.bucket.name)
    }
}