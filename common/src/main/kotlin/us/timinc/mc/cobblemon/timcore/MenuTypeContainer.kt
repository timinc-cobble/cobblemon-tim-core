package us.timinc.mc.cobblemon.timcore

import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType

open class MenuTypeContainer<T : AbstractContainerMenu>(
    private val menuBuilder: MenuType.MenuSupplier<T>,
    val requiredFeatures: FeatureFlagSet = FeatureFlags.DEFAULT_FLAGS,
) {
    val type by lazy {
        MenuType(menuBuilder, requiredFeatures)
    }
}
