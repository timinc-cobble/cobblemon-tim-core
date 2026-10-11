package us.timinc.mc.cobblemon.timcore.mixin.helper

import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import us.timinc.mc.cobblemon.timcore.TimCore

object FossilTweaksHelper {
    fun clientTimeRemaining(remaining: Int, duration: Int): Int =
        if (remaining <= 0 || duration == 14400) remaining
        else kotlin.math.ceil(remaining.toDouble() * 14400 / duration.coerceAtLeast(8)).toInt()
            .coerceIn(clientSyncInterval(duration) + 1, 14400)

    fun clientSyncInterval(duration: Int): Int = (duration.coerceAtLeast(8) / 200).coerceIn(1, 20)

    fun calculateTime(fossilMultiblockStructure: FossilMultiblockStructure): Int {
        return TimCore.config.fossilMachineResurrectionTime.coerceAtLeast(8)
    }
}
