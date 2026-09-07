package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.timinc.mc.cobblemon.timcore.mixin.helper.CancelThrownPokeballInBattleHelper;

@Mixin(EmptyPokeBallEntity.class)
public abstract class CancelThrownPokeballInBattle extends ThrowableItemProjectile {
    @Shadow
    @Final
    private static EntityDataAccessor<Byte> CAPTURE_STATE;

    public CancelThrownPokeballInBattle(EntityType<? extends ThrowableItemProjectile> entityType, double d, double e, double f, Level level) {
        super(entityType, d, e, f, level);
    }

    @Shadow(remap = false)
    protected abstract void drop();

    @Inject(method = "onHitEntity", at = @At(value = "HEAD"), cancellable = true, remap = false)
    void cancelHitEntityInBattle(EntityHitResult hitResult, CallbackInfo ci) {
        if (EmptyPokeBallEntity.CaptureState.getEntries().get(this.entityData.get(CAPTURE_STATE).intValue()) == EmptyPokeBallEntity.CaptureState.NOT) {
            if (hitResult.getEntity() instanceof PokemonEntity entity && !this.level().isClientSide) {
                if (entity.isBattling()) {
                    if (CancelThrownPokeballInBattleHelper.INSTANCE.checkForCancel((EmptyPokeBallEntity) ((Object) this), entity)) {
                        this.drop();
                        ci.cancel();
                    }
                }
            }
        }
    }
}
