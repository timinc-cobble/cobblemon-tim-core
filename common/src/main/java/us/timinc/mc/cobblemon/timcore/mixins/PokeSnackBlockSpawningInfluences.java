package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.block.entity.PokeSnackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.timinc.mc.cobblemon.timcore.mixin.helper.PokeSnackBlockSpawningInfluencesHelper;

@Mixin(com.cobblemon.mod.common.block.PokeSnackBlock.class)
public class PokeSnackBlockSpawningInfluences {
    @Unique
    Boolean cobblemon_tim_core$injected = false;

    @Inject(method = "randomTick", at = @At("TAIL"), remap = false)
    private void cobblemon_tim_core$injectSpawnInfluences(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (cobblemon_tim_core$injected) return;
        cobblemon_tim_core$injected = true;
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity == null) return;
        if (!(entity instanceof PokeSnackBlockEntity)) return;

        PokeSnackBlockSpawningInfluencesHelper.INSTANCE.handle((PokeSnackBlockEntity) entity);
    }
}
