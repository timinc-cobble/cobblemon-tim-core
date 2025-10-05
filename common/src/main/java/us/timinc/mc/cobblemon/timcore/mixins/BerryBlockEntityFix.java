package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.api.berry.*;
import com.cobblemon.mod.common.block.entity.*;
import kotlin.*;
import net.minecraft.resources.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import us.timinc.mc.cobblemon.timcore.mixin.helper.*;

import java.util.*;

@Mixin(BerryBlockEntity.class)
public abstract class BerryBlockEntityFix {
    @Shadow
    private String growthPointSequence;

    @Final
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