package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.timinc.mc.cobblemon.timcore.TimCore;
import us.timinc.mc.cobblemon.timcore.mixin.helper.TickPokemonEntityHelper;

@Mixin(PokemonEntity.class)
public class TickPokemonEntity {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onPokemonEntityTick(CallbackInfo ci) {
        TimCore.INSTANCE.getDebugger().debug("test tick mixin for PokemonEntity");
        PokemonEntity self = (PokemonEntity) (Object) this;
        TickPokemonEntityHelper.INSTANCE.tick(self);
    }
}
