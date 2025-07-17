package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.entity.pokeball.*;
import com.cobblemon.mod.common.entity.pokemon.*;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import us.timinc.mc.cobblemon.timcore.mixin.helper.*;

@Mixin(EmptyPokeBallEntity.class)
public abstract class CancelThrownPokeballInBattle extends ThrowableItemProjectile {
    @Shadow
    @Final
    private static EntityDataAccessor<Byte> CAPTURE_STATE;

    public CancelThrownPokeballInBattle(EntityType<? extends ThrowableItemProjectile> entityType, double d, double e, double f, Level level) {
        super(entityType, d, e, f, level);
    }

    @Shadow
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
