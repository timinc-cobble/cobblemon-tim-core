package us.timinc.mc.cobblemon.timcore

import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties

open class ItemContainer<T : Item>(
    private val itemBuilder: (() -> T),
    open val tab: ResourceKey<CreativeModeTab>? = null,
) {
    class Basic(
        private val itemProperties: Properties = Properties(),
        override val tab: ResourceKey<CreativeModeTab>? = null,
    ) : ItemContainer<Item>({ Item(itemProperties) }, tab)

    val item by lazy {
        itemBuilder()
    }
}
