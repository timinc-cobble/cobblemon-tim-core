package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.spawning.SpawnBucket
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

fun SpawnBucket.getTranslatedName(): MutableComponent? =
    Component.translatable(TimCore.getBucketKeyFromName(this.name))
