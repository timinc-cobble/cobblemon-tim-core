package us.timinc.mc.cobblemon.timcore.fabric

import com.google.gson.JsonElement
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import us.timinc.mc.cobblemon.timcore.AbstractReloadListener

class FabricReloadListener(
    @Suppress("MemberVisibilityCanBePrivate") val reloadListener: AbstractReloadListener,
    @Suppress("MemberVisibilityCanBePrivate") val fabricMod: AbstractFabricMod,
) : SimpleJsonResourceReloadListener(reloadListener.gson, reloadListener.name), IdentifiableResourceReloadListener {
    override fun apply(
        mapObject: MutableMap<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profilerFiller: ProfilerFiller,
    ) = reloadListener.apply(mapObject, resourceManager, profilerFiller)

    override fun getFabricId(): ResourceLocation = fabricMod.mod.modResource(reloadListener.name)

}