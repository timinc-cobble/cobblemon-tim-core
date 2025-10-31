package us.timinc.mc.cobblemon.timcore.mixins;

@org.spongepowered.asm.mixin.Mixin(com.cobblemon.mod.common.pokemon.Pokemon.class)
public class IsActuallyWild {
    @org.spongepowered.asm.mixin.injection.Inject(method = "isWild", at = @org.spongepowered.asm.mixin.injection.At("HEAD"), remap = false, cancellable = true)
    void isActuallyWild(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
        com.cobblemon.mod.common.pokemon.Pokemon pokemon = (com.cobblemon.mod.common.pokemon.Pokemon) ((Object) this);
        if (pokemon.getOriginalTrainerType() != com.cobblemon.mod.common.pokemon.OriginalTrainerType.NONE) {
            cir.setReturnValue(false);
        }
    }
}
