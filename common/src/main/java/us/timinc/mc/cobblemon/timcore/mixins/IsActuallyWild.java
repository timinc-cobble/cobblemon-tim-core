package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.pokemon.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

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
