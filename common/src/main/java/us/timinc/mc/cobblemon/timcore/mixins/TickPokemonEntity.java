package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.entity.pokemon.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import us.timinc.mc.cobblemon.timcore.mixin.helper.*;

@Mixin(PokemonEntity.class)
public class TickPokemonEntity {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At("HEAD"))
    private void onPokemonEntityTick(CallbackInfo ci) {
        PokemonEntity self = (PokemonEntity) (Object) this;
        TickPokemonEntityHelper.INSTANCE.tick(self);
    }
}
