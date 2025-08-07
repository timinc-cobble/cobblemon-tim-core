package us.timinc.mc.cobblemon.timcore.event

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer

interface CheckExpAllEvent {
    val player: ServerPlayer
    val pokemon: Pokemon
    var hasExpAll: Boolean

    class Check(override val player: ServerPlayer, override val pokemon: Pokemon, override var hasExpAll: Boolean) :
        CheckExpAllEvent
}