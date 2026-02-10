package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.timinc.mc.cobblemon.timcore.TimCore;
import us.timinc.mc.cobblemon.timcore.mixin.helper.PokeBallBreakingHelper;

@Mixin(EmptyPokeBallEntity.class)
public abstract class PokeBallBreaking extends ThrowableItemProjectile {
    public PokeBallBreaking(EntityType<? extends ThrowableItemProjectile> entityType, double d, double e, double f, Level level) {
        super(entityType, d, e, f, level);
    }

    @Inject(method = "onHitBlock", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/entity/pokeball/EmptyPokeBallEntity;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"), remap = false, cancellable = true)
    void TimCore$breakOnHitBlock(@NotNull BlockHitResult hitResult, CallbackInfo ci) {
        PokeBallBreakingHelper.INSTANCE.handle(ci, (EmptyPokeBallEntity) (Object) this, TimCore.DataKeys.PokeBallBreakReasons.HIT_BLOCK, hitResult.getBlockPos());
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/entity/pokeball/EmptyPokeBallEntity;drop()V", ordinal = 0), remap = false, cancellable = true)
    void TimCore$breakOnHitOwnedPokemon(EntityHitResult entityHitResult, CallbackInfo ci) {
        PokeBallBreakingHelper.INSTANCE.handle(ci, (EmptyPokeBallEntity) (Object) this, TimCore.DataKeys.PokeBallBreakReasons.HIT_OWNED, null);
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/entity/pokeball/EmptyPokeBallEntity;drop()V", ordinal = 1), remap = false, cancellable = true)
    void TimCore$breakOnHitUncatchablePokemon(EntityHitResult entityHitResult, CallbackInfo ci) {
        PokeBallBreakingHelper.INSTANCE.handle(ci, (EmptyPokeBallEntity) (Object) this, TimCore.DataKeys.PokeBallBreakReasons.HIT_UNCATCHABLE, null);
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/entity/pokeball/EmptyPokeBallEntity;drop()V", ordinal = 2), remap = false, cancellable = true)
    void TimCore$breakOnHitInBattle(EntityHitResult entityHitResult, CallbackInfo ci) {
        PokeBallBreakingHelper.INSTANCE.handle(ci, (EmptyPokeBallEntity) (Object) this, TimCore.DataKeys.PokeBallBreakReasons.HIT_WILD_OTHER_BATTLE, null);
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/entity/pokeball/EmptyPokeBallEntity;drop()V", ordinal = 4), remap = false, cancellable = true)
    void TimCore$breakOnHitNotSingles(EntityHitResult entityHitResult, CallbackInfo ci) {
        PokeBallBreakingHelper.INSTANCE.handle(ci, (EmptyPokeBallEntity) (Object) this, TimCore.DataKeys.PokeBallBreakReasons.HIT_WILD_NOT_SINGLES, null);
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/entity/pokeball/EmptyPokeBallEntity;drop()V", ordinal = 5), remap = false, cancellable = true)
    void TimCore$breakOnHitNotYourTurn(EntityHitResult entityHitResult, CallbackInfo ci) {
        PokeBallBreakingHelper.INSTANCE.handle(ci, (EmptyPokeBallEntity) (Object) this, TimCore.DataKeys.PokeBallBreakReasons.HIT_WILD_NOT_TURN, null);
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/entity/pokeball/EmptyPokeBallEntity;drop()V", ordinal = 6), remap = false, cancellable = true)
    void TimCore$breakOnHitBusy(EntityHitResult entityHitResult, CallbackInfo ci) {
        PokeBallBreakingHelper.INSTANCE.handle(ci, (EmptyPokeBallEntity) (Object) this, TimCore.DataKeys.PokeBallBreakReasons.HIT_BUSY, null);
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/entity/pokeball/EmptyPokeBallEntity;drop()V", ordinal = 7), remap = false, cancellable = true)
    void TimCore$breakOnHitNonTargetWild(EntityHitResult entityHitResult, CallbackInfo ci) {
        PokeBallBreakingHelper.INSTANCE.handle(ci, (EmptyPokeBallEntity) (Object) this, TimCore.DataKeys.PokeBallBreakReasons.HIT_NON_TARGET_WILD, null);
    }
}
