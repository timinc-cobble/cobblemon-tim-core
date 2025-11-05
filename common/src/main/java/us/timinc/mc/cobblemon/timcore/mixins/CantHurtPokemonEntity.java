package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.entity.pokemon.*;
import net.minecraft.world.damagesource.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import us.timinc.mc.cobblemon.timcore.mixin.helper.*;

@Mixin(PokemonEntity.class)
public class CantHurtPokemonEntity {

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), remap = false, cancellable = true)
    private void isInvulnerableBecauseOwned(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (CantHurtPokemonEntityHelper.INSTANCE
                .cantBeHurtBecauseOwned((PokemonEntity) (Object) this, damageSource)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), remap = false, cancellable = true)
    private void isInvulnerableToInGeneral(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (CantHurtPokemonEntityHelper.INSTANCE.cantBeHurtInGeneral(damageSource)) {
            cir.setReturnValue(true);
        }
    }
}

