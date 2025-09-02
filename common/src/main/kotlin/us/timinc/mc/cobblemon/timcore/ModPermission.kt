package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.permission.Permission
import com.cobblemon.mod.common.api.permission.PermissionLevel
import net.minecraft.resources.ResourceLocation

data class ModPermission(
    private val node: String,
    override val level: PermissionLevel,
    val mod: AbstractMod<*>,
) : Permission {
    override val identifier: ResourceLocation = mod.modResource(node)

    override val literal: String = "${mod.modId}.${node}"
}