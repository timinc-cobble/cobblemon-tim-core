package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.timcore.TimCore.Tags.EXP_ALL
import us.timinc.mc.cobblemon.timcore.TimCore.config
import us.timinc.mc.cobblemon.timcore.event.CheckExpAllEvent

fun ServerPlayer.hasExpAllFor(pokemon: Pokemon? = null): Boolean {
    val event =
        CheckExpAllEvent.Check(this, this.inventory.items.any { it.`is`(EXP_ALL) } || config.forceExpAll, pokemon)
    TimCoreEvents.CHECK_EXP_ALL.post(event)
    return event.hasExpAll
}