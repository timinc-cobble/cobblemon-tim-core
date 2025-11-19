package us.timinc.mc.cobblemon.timcore.event

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer

interface CheckExpAllEvent {
    val player: ServerPlayer
    var hasExpAll: Boolean
    val pokemon: Pokemon?

    class Check(override val player: ServerPlayer, override var hasExpAll: Boolean, override val pokemon: Pokemon? = null) :
        CheckExpAllEvent
}