package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.timinc.mc.cobblemon.timcore.mixin.helper.FossilTweaksHelper;

@Mixin(FossilMultiblockStructure.class)
public class FossilTweaks {
    @Shadow(remap = false)
    private int timeRemaining;

    @Inject(method = "startMachine", at = @At("TAIL"), remap = false)
    private void startMachineMixin(Level world, CallbackInfo ci) {
        this.timeRemaining = FossilTweaksHelper.INSTANCE.calculateTime((FossilMultiblockStructure) ((Object) this));
    }
}
