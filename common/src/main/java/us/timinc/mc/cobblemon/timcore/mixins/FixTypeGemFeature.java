package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.world.feature.TypeGemFeature;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TypeGemFeature.class)
public class FixTypeGemFeature {
    @ModifyArgs(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 0
            )
    )
    private void placeInitialGem(Args args, @Local(name = "chosenPos") BlockPos chosenPos, @Local(name = "gemState") BlockState gemState) {
        args.set(0, chosenPos);
        args.set(1, gemState);
    }
}
