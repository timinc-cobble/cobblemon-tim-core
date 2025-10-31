package us.timinc.mc.cobblemon.timcore.mixins;

@org.spongepowered.asm.mixin.Mixin(com.cobblemon.mod.common.entity.pokemon.PokemonEntity.class)
public class CantHurtPokemonEntity {
    @org.spongepowered.asm.mixin.injection.Inject(method = "isInvulnerableTo", at = @org.spongepowered.asm.mixin.injection.At(value = "HEAD"), remap = false, cancellable = true)
    void isInvulnerableBecauseOwned(net.minecraft.world.damagesource.DamageSource damageSource, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
        if (us.timinc.mc.cobblemon.timcore.mixin.helper.CantHurtPokemonEntityHelper.INSTANCE.cantBeHurtBecauseOwned((com.cobblemon.mod.common.entity.pokemon.PokemonEntity) ((Object) this), damageSource)) {
            cir.setReturnValue(true);
        }
    }

    @org.spongepowered.asm.mixin.injection.Inject(method = "isInvulnerableTo", at = @org.spongepowered.asm.mixin.injection.At(value = "HEAD"), remap = false, cancellable = true)
    void isInvulnerableToInGeneral(net.minecraft.world.damagesource.DamageSource damageSource, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
        if (us.timinc.mc.cobblemon.timcore.mixin.helper.CantHurtPokemonEntityHelper.INSTANCE.cantBeHurtInGeneral(damageSource)) {
            cir.setReturnValue(true);
        }
    }
}
