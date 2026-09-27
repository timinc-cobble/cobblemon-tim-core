package us.timinc.mc.cobblemon.timcore

import com.mojang.datafixers.types.Type
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

open class BlockEntityTypeContainer<T : BlockEntity>(
    private val blockEntityBuilder: (BlockPos, BlockState) -> T,
    val validBlocks: List<BlockContainer<out Block>>,
    val datafixerType: Type<*>? = null,
) {
    val type by lazy {
        BlockEntityType.Builder.of(
            { pos, state -> blockEntityBuilder(pos, state) },
            *validBlocks.map { it.block }.toTypedArray(),
        ).build(datafixerType)
    }
}
