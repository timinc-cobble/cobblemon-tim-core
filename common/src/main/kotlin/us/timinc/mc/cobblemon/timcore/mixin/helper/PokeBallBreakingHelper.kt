package us.timinc.mc.cobblemon.timcore.mixin.helper

import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import us.timinc.mc.cobblemon.timcore.TimCore
import us.timinc.mc.cobblemon.timcore.TimCoreEvents
import us.timinc.mc.cobblemon.timcore.event.PokeBallBreakEvent
import us.timinc.mc.cobblemon.timcore.feature.PokeBallBreaking
import kotlin.random.Random.Default.nextFloat

object PokeBallBreakingHelper {
    fun handle(ci: CallbackInfo, pokeBallEntity: EmptyPokeBallEntity, reason: String, hitBlockPos: BlockPos? = null) {
        if (PokeBallBreaking.shouldBreak(pokeBallEntity, reason, hitBlockPos)) ci.cancel()
    }
}