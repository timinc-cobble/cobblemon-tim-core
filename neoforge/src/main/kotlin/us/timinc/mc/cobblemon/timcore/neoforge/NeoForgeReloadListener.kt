package us.timinc.mc.cobblemon.timcore.neoforge

import com.google.gson.JsonElement
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import us.timinc.mc.cobblemon.timcore.AbstractReloadListener

class NeoForgeReloadListener(
    @Suppress("MemberVisibilityCanBePrivate") val reloadListener: AbstractReloadListener,
) : SimpleJsonResourceReloadListener(reloadListener.gson, reloadListener.name) {
    override fun apply(
        mapObject: MutableMap<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profilerFiller: ProfilerFiller,
    ) = reloadListener.apply(mapObject, resourceManager, profilerFiller)
}