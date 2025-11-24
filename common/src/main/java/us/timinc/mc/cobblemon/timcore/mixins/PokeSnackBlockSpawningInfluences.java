package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.block.entity.PokeSnackBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.timinc.mc.cobblemon.timcore.mixin.helper.PokeSnackBlockSpawningInfluencesHelper;

@Mixin(PokeSnackBlockEntity.class)
public class PokeSnackBlockSpawningInfluences {
    @Inject(method = "randomTick", at = @At("HEAD"), remap = false)
    private void cobblemon_tim_core$injectSpawnInfluences(CallbackInfo ci) {
        PokeSnackBlockSpawningInfluencesHelper.INSTANCE.handle((PokeSnackBlockEntity) (Object) this);
    }
}
