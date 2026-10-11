package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import us.timinc.mc.cobblemon.timcore.mixin.helper.FossilTweaksHelper;

@SuppressWarnings("DataFlowIssue")
@Mixin(FossilMultiblockStructure.class)
public class FossilTweaks {
    @Inject(method = "tick", at = @At("TAIL"), remap = false)
    private void tim_core$syncGrowth(Level world, CallbackInfo ci) {
        if (world.isClientSide) return;
        FossilMultiblockStructure structure = (FossilMultiblockStructure) (Object) this;
        int remaining = structure.getTimeRemaining();
        int duration = FossilTweaksHelper.INSTANCE.calculateTime(structure);
        if (remaining <= 0 || duration == 14400) return;
        if (remaining % (duration / 8) == 0) return;
        int interval = FossilTweaksHelper.INSTANCE.clientSyncInterval(duration);
        if ((duration - remaining) % interval != 0) return;
        BlockEntity controller = world.getBlockEntity(structure.getControllerBlockPos());
        if (controller != null) {
            world.sendBlockUpdated(controller.getBlockPos(), controller.getBlockState(), controller.getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @ModifyConstant(method = {"startMachine", "updateProgress", "getAnalogOutputSignal"},
            constant = @Constant(intValue = 14400), remap = false)
    private int tim_core$resurrectionTime(int original) {
        return FossilTweaksHelper.INSTANCE.calculateTime((FossilMultiblockStructure) (Object) this);
    }

    @ModifyConstant(method = {"tick", "updateProgress"},
            constant = @Constant(intValue = 1800), remap = false)
    private int tim_core$stageTime(int original) {
        return FossilTweaksHelper.INSTANCE.calculateTime((FossilMultiblockStructure) (Object) this) / 8;
    }
}
