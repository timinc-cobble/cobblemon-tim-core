package us.timinc.mc.cobblemon.timcore

import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

open class BlockContainer<B : Block>(
    private val blockBuilder: () -> B,
    open val itemProperties: Item.Properties = Item.Properties(),
    open val ignoreItem: Boolean = false,
    open val tab: ResourceKey<CreativeModeTab>? = null,
) {
    class Basic(
        private val blockProperties: BlockBehaviour.Properties,
        override val itemProperties: Item.Properties = Item.Properties(),
        override val ignoreItem: Boolean = false,
        override val tab: ResourceKey<CreativeModeTab>? = null,
    ) : BlockContainer<Block>(
        { Block(blockProperties) },
        itemProperties,
        ignoreItem,
        tab
    )

    val block by lazy {
        blockBuilder()
    }
    val item by lazy {
        if (ignoreItem) return@lazy null
        BlockItem(block, itemProperties)
    }
}