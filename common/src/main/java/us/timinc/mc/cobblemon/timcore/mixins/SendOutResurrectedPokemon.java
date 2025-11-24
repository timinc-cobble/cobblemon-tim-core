package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.timinc.mc.cobblemon.timcore.mixin.helper.SendOutResurrectedPokemonHelper;

@Mixin(FossilMultiblockStructure.class)
public class SendOutResurrectedPokemon {
    @Inject(method = "spawn", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/api/reactive/EventObservable;emit([Ljava/lang/Object;)V"), remap = false)
    private void cobblemon_tim_core$attachPokemonEntityToPokemon(Level world, BlockPos pos, Direction directionToBehind, Pokemon pokemon, CallbackInfoReturnable<Boolean> cir) {
        SendOutResurrectedPokemonHelper.INSTANCE.handle(pokemon, world, pos);
    }
}
