package us.timinc.mc.cobblemon.timcore

import com.google.gson.Gson
import com.google.gson.JsonElement
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller

abstract class AbstractReloadListener(val gson: Gson, val name: String) {
    abstract fun apply(
        objectMap: MutableMap<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profilerFiller: ProfilerFiller,
    )
}