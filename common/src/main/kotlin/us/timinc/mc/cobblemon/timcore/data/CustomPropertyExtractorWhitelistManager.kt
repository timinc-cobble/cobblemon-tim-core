package us.timinc.mc.cobblemon.timcore.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.UnboundedMapCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import us.timinc.mc.cobblemon.timcore.AbstractReloadListener

object CustomPropertyExtractorWhitelistManager : AbstractReloadListener(Gson(), "tim_core/custom_property_whitelist") {
    val CODEC: UnboundedMapCodec<String, Boolean> = Codec.unboundedMap(Codec.STRING, Codec.BOOL)

    data class Entry(
        val name: String,
        val context: String,
    )

    private val whitelist: MutableMap<Entry, Boolean> = mutableMapOf()

    override fun apply(
        objectMap: MutableMap<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profilerFiller: ProfilerFiller,
    ) {
        whitelist.clear()
        objectMap.entries.forEach { (id, json) ->
            val raw = CODEC.parse(JsonOps.INSTANCE, json).orThrow
            raw.entries.forEach { (k, v) ->
                whitelist[Entry(id.path, k)] = v
            }
        }
    }

    fun isAllowed(name: String, context: String): Boolean = whitelist[Entry(name, context)] ?: false
}