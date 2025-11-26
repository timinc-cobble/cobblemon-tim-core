package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.pokemon.Pokemon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.timinc.mc.cobblemon.timcore.mixin.helper.PokemonPropertiesForcedAspectsHelper;

@Mixin(PokemonProperties.class)
public class PokemonPropertiesForcedAspects {
    @Inject(method = "commonApply", at = @At("HEAD"), remap = false)
    private void cobblemon_tim_core$applyAspects(Pokemon pokemon, CallbackInfo ci) {
        PokemonPropertiesForcedAspectsHelper.INSTANCE.getNewAspects(pokemon, (PokemonProperties) (Object) this);
    }
}
