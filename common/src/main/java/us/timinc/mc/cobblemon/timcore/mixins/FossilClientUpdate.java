package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.api.multiblock.MultiblockEntity;
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure;
import com.cobblemon.mod.common.util.DataKeys;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.timinc.mc.cobblemon.timcore.mixin.helper.FossilTweaksHelper;

@Mixin(MultiblockEntity.class)
public class FossilClientUpdate {
    @Inject(method = "getUpdateTag", at = @At("RETURN"))
    private void tim_core$scaleClientTimer(HolderLookup.Provider lookup, CallbackInfoReturnable<CompoundTag> cir) {
        MultiblockEntity entity = (MultiblockEntity) (Object) this;
        if (entity.getLevel() == null || entity.getLevel().isClientSide) return;
        if (!(entity.getMultiblockStructure() instanceof FossilMultiblockStructure fossil)) return;
        CompoundTag tag = cir.getReturnValue();
        if (!tag.contains(DataKeys.MULTIBLOCK_STORAGE)) return;
        CompoundTag structure = tag.getCompound(DataKeys.MULTIBLOCK_STORAGE);
        structure.putInt(DataKeys.TIME_LEFT, FossilTweaksHelper.INSTANCE.clientTimeRemaining(
                fossil.getTimeRemaining(), FossilTweaksHelper.INSTANCE.calculateTime(fossil)));
    }
}
