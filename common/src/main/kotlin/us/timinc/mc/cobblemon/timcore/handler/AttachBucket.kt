package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCore.DataKeys.SPAWNED_IN_BUCKET
import us.timinc.mc.cobblemon.timcore.TimCore.config
import us.timinc.mc.cobblemon.timcore.event.EntityDidSpawnEvent

object AttachBucket : AbstractHandler<EntityDidSpawnEvent<PokemonEntity>>() {
    override fun handle(evt: EntityDidSpawnEvent<PokemonEntity>) {
        if (!config.addBucketToData) return
        evt.entity.pokemon.persistentData.putString(SPAWNED_IN_BUCKET, evt.ctx.cause.bucket.name)
    }
}