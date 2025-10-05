package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.api.berry.Berry;
import com.cobblemon.mod.common.api.berry.GrowthPoint;
import com.cobblemon.mod.common.block.entity.BerryBlockEntity;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.timinc.mc.cobblemon.timcore.mixin.helper.BerryBlockEntityFixHelper;
import kotlin.Pair;
import java.util.ArrayList;
import java.util.List;

@Mixin(BerryBlockEntity.class)
public abstract class BerryBlockEntityFix {
    @Shadow
    private String growthPointSequence;

    @org.spongepowered.asm.mixin.Final
    @Shadow
    private ArrayList<ResourceLocation> growthPoints;

    @Shadow
    public abstract Berry berry();

    @Inject(method = "berryAndGrowthPoint$common", at = @At(value = "HEAD"), remap = false, cancellable = true)
    void fixBerryAndGrowthPoint(CallbackInfoReturnable<List<Pair<Berry, GrowthPoint>>> cir) {
        cir.setReturnValue(BerryBlockEntityFixHelper.process(this.berry(), this.growthPointSequence, this.growthPoints));
        cir.cancel();
    }
}