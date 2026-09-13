package us.timinc.mc.cobblemon.timcore.mixins;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.command.TestPartySlotCommand;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TestPartySlotCommand.class)
public class FixTestPartySlotCommand {
    @Inject(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/cobblemon/mod/common/pokemon/Pokemon;createPokemonProperties(Ljava/util/List;)Lcom/cobblemon/mod/common/api/pokemon/PokemonProperties;"
            ),
            cancellable = true,
            remap = false
    )
    private void usePokemonMatcher(
            CommandContext<CommandSourceStack> context,
            CallbackInfoReturnable<Integer> cir,
            @Local PokemonProperties properties,
            @Local Pokemon target
    ) {
        cir.setReturnValue(properties.matches(target) ? Command.SINGLE_SUCCESS : 0);
    }
}
