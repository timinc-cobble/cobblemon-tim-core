package us.timinc.mc.cobblemon.timcore.mixins;

@org.spongepowered.asm.mixin.Mixin(com.cobblemon.mod.common.entity.pokemon.PokemonEntity.class)
public class TickPokemonEntity {
    @org.spongepowered.asm.mixin.injection.Inject(method = "tick", at = @org.spongepowered.asm.mixin.injection.At("HEAD"))
    void onPokemonEntityTick(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        com.cobblemon.mod.common.entity.pokemon.PokemonEntity me = (com.cobblemon.mod.common.entity.pokemon.PokemonEntity) ((Object) this);
        us.timinc.mc.cobblemon.timcore.mixin.helper.TickPokemonEntity.INSTANCE.tick(me);
    }
}
