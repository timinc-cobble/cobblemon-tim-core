package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.api.reactive.EventObservable;
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.timinc.mc.cobblemon.timcore.mixin.helper.CancelThrownPokeballInBattleHelper;

@Mixin(EmptyPokeBallEntity.class)
public abstract class CancelThrownPokeballInBattle {
    @Unique
    private boolean tim_core$postedBattleHit;

    @Shadow(remap = false)
    protected abstract void drop();

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void tim_core$resetHitEvent(EntityHitResult hitResult, CallbackInfo ci) {
        tim_core$postedBattleHit = false;
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target =
            "Lcom/cobblemon/mod/common/api/battles/model/PokemonBattle;getCaptureActions()Ljava/util/List;",
            remap = false), cancellable = true)
    private void tim_core$checkBattleHit(EntityHitResult hitResult, CallbackInfo ci) {
        PokemonEntity pokemonEntity = (PokemonEntity) hitResult.getEntity();
        tim_core$postedBattleHit = true;
        if (CancelThrownPokeballInBattleHelper.INSTANCE.checkForCancel((EmptyPokeBallEntity) (Object) this, pokemonEntity)) {
            drop();
            ci.cancel();
        }
    }

    @WrapWithCondition(method = "onHitEntity", at = @At(value = "INVOKE", target =
            "Lcom/cobblemon/mod/common/api/reactive/EventObservable;emit([Ljava/lang/Object;)V",
            remap = false))
    private boolean tim_core$emitHitOnce(EventObservable<?> observable, Object[] events) {
        return !tim_core$postedBattleHit;
    }
}
