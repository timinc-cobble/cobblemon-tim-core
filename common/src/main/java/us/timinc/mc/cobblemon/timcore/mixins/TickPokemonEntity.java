package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.timinc.mc.cobblemon.timcore.mixin.helper.TickPokemonEntityHelper;

@Mixin(PokemonEntity.class)
public class TickPokemonEntity {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At("HEAD"))
    private void onPokemonEntityTick(CallbackInfo ci) {
        PokemonEntity self = (PokemonEntity) (Object) this;
        TickPokemonEntityHelper.INSTANCE.tick(self);
    }
}
