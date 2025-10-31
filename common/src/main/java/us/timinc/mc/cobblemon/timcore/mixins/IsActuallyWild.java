package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.pokemon.OriginalTrainerType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Pokemon.class)
public class IsActuallyWild {

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "isWild", at = @At("HEAD"), remap = false, cancellable = true)
    private void isActuallyWild(CallbackInfoReturnable<Boolean> cir) {
        Pokemon pokemon = (Pokemon) (Object) this;
        if (pokemon.getOriginalTrainerType() != OriginalTrainerType.NONE) {
            cir.setReturnValue(false);
        }
    }
}
