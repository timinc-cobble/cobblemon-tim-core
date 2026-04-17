package us.timinc.mc.cobblemon.timcore.codec

import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.mojang.serialization.Codec
import net.minecraft.resources.ResourceLocation

@Suppress("unused")
fun makeResourceLocationWithDefaultNamespaceCodec(defaultNamespace: String): Codec<ResourceLocation> =
    Codec.STRING.xmap(
        { it.asIdentifierDefaultingNamespace(defaultNamespace) },
        { it.toString() }
    )