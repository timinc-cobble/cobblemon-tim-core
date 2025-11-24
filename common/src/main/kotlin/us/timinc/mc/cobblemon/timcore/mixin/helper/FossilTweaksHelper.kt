package us.timinc.mc.cobblemon.timcore.mixin.helper

import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure
import us.timinc.mc.cobblemon.timcore.TimCore

object FossilTweaksHelper {
    fun calculateTime(fossilMultiblockStructure: FossilMultiblockStructure): Int {
        return TimCore.config.fossilMachineResurrectionTime
    }
}