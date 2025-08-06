package us.timinc.mc.cobblemon.timcore

import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.timcore.TimCore.Tags.EXP_ALL

fun ServerPlayer.hasExpAll() = this.inventory.items.any { it.`is`(EXP_ALL) }