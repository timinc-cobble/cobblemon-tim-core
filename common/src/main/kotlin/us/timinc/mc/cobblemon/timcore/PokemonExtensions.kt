package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.timcore.TimCore.debugger
import java.util.UUID

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

fun Pokemon.getBucketTranslationKey(): Component =
    Component.translatable(TimCore.getBucketKeyFromName(this.getBucket()))

fun Pokemon.getSpawnCause(): String? = persistentData.getStringOrNull(TimCore.DataKeys.SPAWNED_VIA)

fun Pokemon.reserveFor(player: String) {
    TimCore.CustomPokemonProperties.RESERVED_FOR.pokemonApplicator(this, player)
}

fun Pokemon.reserveFor(player: UUID) {
    this.reserveFor(player.toString())
}

fun Pokemon.reserveFor(player: ServerPlayer) {
    reserveFor(player.uuid)
}

fun Pokemon.getReservedFor(): String? = persistentData.getStringOrNull(TimCore.DataKeys.RESERVED_FOR)

enum class ReservationType {
    RESERVED_FOR,
    UNRESERVED,
    RESERVED_FOR_OTHER
}

fun Pokemon.isReservedFor(uuid: String): ReservationType {
    val reservedFor = getReservedFor() ?: return ReservationType.UNRESERVED
    return if (uuid == reservedFor) ReservationType.RESERVED_FOR else ReservationType.RESERVED_FOR_OTHER
}

fun Pokemon.isReservedFor(uuid: UUID): ReservationType = isReservedFor(uuid.toString())

fun Pokemon.isReservedFor(player: ServerPlayer): ReservationType = isReservedFor(player.uuid)

