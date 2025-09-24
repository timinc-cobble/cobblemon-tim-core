package us.timinc.mc.cobblemon.timcore

import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.resources.ResourceLocation

class CommandArgumentContainer<A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>>(
    val identifier: ResourceLocation,
    val argumentClass: Class<A>,
    val info: ArgumentTypeInfo<A, T>,
)