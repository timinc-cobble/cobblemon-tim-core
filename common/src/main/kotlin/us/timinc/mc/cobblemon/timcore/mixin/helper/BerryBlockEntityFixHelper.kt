package us.timinc.mc.cobblemon.timcore.mixin.helper

import com.cobblemon.mod.common.api.berry.Berries
import com.cobblemon.mod.common.api.berry.Berry
import com.cobblemon.mod.common.api.berry.GrowthPoint
import net.minecraft.resources.ResourceLocation
import kotlin.math.min

object BerryBlockEntityFixHelper {
    @JvmStatic
    fun process(
        berry: Berry?,
        growthPointSequence: String,
        growthPoints: ArrayList<ResourceLocation>,
    ): List<Pair<Berry, GrowthPoint>> {
        val baseBerry = berry ?: return emptyList()
        val berryPoints = arrayListOf<Pair<Berry, GrowthPoint>>()
        val sequenceIndices =
            growthPointSequence.toCharArray().filter { it.digitToInt(16) < baseBerry.growthPoints.size }
        for ((index, identifier) in growthPoints.withIndex()) {
            val internalBerry = Berries.getByIdentifier(identifier) ?: continue
            val sequenceIndexHex = sequenceIndices[min(index, sequenceIndices.size - 1)].digitToInt(16)
            berryPoints.add(internalBerry to baseBerry.growthPoints[sequenceIndexHex])
        }
        return berryPoints
    }
}