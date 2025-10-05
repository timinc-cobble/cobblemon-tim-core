package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.entity.pokeball.*;
import com.cobblemon.mod.common.entity.pokemon.*;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import us.timinc.mc.cobblemon.timcore.mixin.helper.*;
import com.cobblemon.mod.common.block.entity.BerryBlockEntity;

@Mixin(BerryBlockEntity.class)
public abstract class BerryBlockEntityFix {
    @Shadow
    private String growthPointSequence;

    @Shadow
    private java.util.ArrayList<net.minecraft.resources.ResourceLocation> growthPoints;

    @Shadow
    protected abstract com.cobblemon.mod.common.api.berry.Berry berry();

    @Inject(method = "berryAndGrowthPoint$common", at = @At(value = "HEAD"), remap = false, cancellable = true)
    void fixBerryAndGrowthPoint(CallbackInfoReturnable<java.util.List<kotlin.Pair<com.cobblemon.mod.common.api.berry.Berry, com.cobblemon.mod.common.api.berry.GrowthPoint>>> cir) {
        cir.setReturnValue(BerryBlockEntityFixHelper.process(this.berry(), this.growthPointSequence, this.growthPoints));
        cir.cancel();
    }
}
