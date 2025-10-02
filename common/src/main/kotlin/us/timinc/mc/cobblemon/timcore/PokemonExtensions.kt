package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.timcore.TimCore.debugger
import java.util.*

fun Pokemon.getIdentifier() = "${getDisplayName().string}[${uuid}]"
fun Pokemon.immuneToQuickBall() = this.persistentData.contains(TimCore.DataKeys.ALREADY_HIT_WITH_QUICK_BALL)
fun Pokemon.getBucket(): String? {
    val bucket = this.persistentData.getStringOrNull(TimCore.DataKeys.SPAWNED_IN_BUCKET)
    if (bucket == null) {
        debugger.debug(
            "Could not determine spawn bucket of ${getIdentifier()}. " +
                    "Common reasons: unnatural spawn, pre-TimCore spawn, or data erased by another mod.",
            true
        )
    }
    return bucket
}

fun Pokemon.reserveFor(stringUuid: String) {
    TimCore.CustomPokemonProperties.RESERVED_FOR.pokemonApplicator(this, stringUuid)
}

fun Pokemon.reserveFor(uuid: UUID) {
    this.reserveFor(uuid.toString())
}

fun Pokemon.reserveFor(player: ServerPlayer) {
    reserveFor(player.uuid)
}