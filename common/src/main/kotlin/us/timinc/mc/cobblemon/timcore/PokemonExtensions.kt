package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.pokemon.Pokemon

fun Pokemon.getIdentifier() = "${getDisplayName().string}[${uuid}]"
fun Pokemon.getBucket() = this.persistentData.getStringOrNull(TimCore.DataKeys.SPAWNED_IN_BUCKET)