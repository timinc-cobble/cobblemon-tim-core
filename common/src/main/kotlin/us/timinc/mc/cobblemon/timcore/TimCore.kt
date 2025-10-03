package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import us.timinc.mc.cobblemon.timcore.handler.*

const val MOD_ID = "tim_core"

object TimCore : AbstractMod<TimCore.Config>(MOD_ID, Config::class.java) {
    class Config : AbstractConfig() {
        val expAllMultiplier: Float = 1.0F
        val enableExpAll: Boolean = true
        val forceExpAll: Boolean = false
        val addBucketToData: Boolean = true
        val addSpawnCauseToData: Boolean = true
        val preventQuickBallSpam: Boolean = true
    }

    object Tags {
        @JvmField
        val EXP_ALL: TagKey<Item> = TagKey.create(Registries.ITEM, modResource("exp_all"))
    }

    object DataKeys {
        const val SPAWNED_IN_BUCKET = "tim_core:spawned_in_bucket"
        const val ALREADY_HIT_WITH_QUICK_BALL = "tim_core:already_hit_with_quick_ball"
        const val RESERVED_FOR = "tim_core:reserved_for"
        const val SPAWNED_VIA = "tim_core:spawned_via"

        object SpawnCauses {
            val FISHING = modResource("fishing")
            val PLAYER_SPAWNER = modResource("player_spawner")
        }
    }

    object CustomPokemonProperties {
        val RESERVED_FOR = CustomStringProperty(DataKeys.RESERVED_FOR)
        val SPAWNED_VIA = CustomStringProperty(DataKeys.SPAWNED_VIA)
    }

    object TranslationComponents {
        fun reserved(pokemon: Pokemon, player: ServerPlayer): MutableComponent =
            Component.translatable("tim_core.feedback.reserved", pokemon.getDisplayName(), player.name)
    }

    init {
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.HIGHEST, ExpAllHandler::handle)
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.HIGHEST, AttachBucket::handle)
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOWEST, AttachSpawnCause::handle)
        CobblemonEvents.POKEMON_CATCH_RATE.subscribe(Priority.LOWEST, PreventQuickBallSpam::handle)
        CobblemonEvents.THROWN_POKEBALL_HIT.subscribe(Priority.NORMAL, PokeballHitReserved::handle)
    }
}