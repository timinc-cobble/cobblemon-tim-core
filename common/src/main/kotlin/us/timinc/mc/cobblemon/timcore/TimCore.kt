package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.spawning.BestSpawner.fishingSpawner
import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawnerFactory
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.getPlayer
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import us.timinc.mc.cobblemon.timcore.handler.*
import us.timinc.mc.cobblemon.timcore.influence.EntityDidSpawn
import us.timinc.mc.cobblemon.timcore.influence.PreventSpawnsInfluence
import java.util.*

const val MOD_ID = "tim_core"

object TimCore : AbstractMod<TimCore.Config>(MOD_ID, Config::class.java) {
    class Config : AbstractConfig() {
        val expAllMultiplier: Float = 1.0F
        val enableExpAll: Boolean = true
        val forceExpAll: Boolean = false
        val addBucketToData: Boolean = true
        val addSpawnCauseToData: Boolean = true
        val preventQuickBallSpam: Boolean = true
        val spawnBlacklist: List<String> = emptyList()
        val spawnWhitelist: List<String> = emptyList()
        val pokemonEntitiesAreInvulnerable: Boolean = false
        val reservedPokemonEntitiesAreInvulnerable: Boolean = true
        val requirePartyToFishPokemon: Boolean = false

        var _spawnBlacklistMatcher: Set<PokemonMatcher>? = null
        val spawnBlacklistMatcher: Set<PokemonMatcher>
            get() {
                return _spawnBlacklistMatcher ?: run {
                    _spawnBlacklistMatcher = spawnBlacklist.map(PokemonMatcher::parse).toSet()
                    return _spawnBlacklistMatcher!!
                }
            }
        var _spawnWhitelistMatcher: Set<PokemonMatcher>? = null
        val spawnWhitelistMatcher: Set<PokemonMatcher>
            get() {
                return _spawnWhitelistMatcher ?: run {
                    _spawnWhitelistMatcher = spawnWhitelist.map(PokemonMatcher::parse).toSet()
                    return _spawnWhitelistMatcher!!
                }
            }
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
        fun reserved(pokemon: Pokemon): MutableComponent =
            Component.translatable(
                "tim_core.feedback.reserved",
                pokemon.getDisplayName(),
                pokemon.getReservedFor()?.let {
                    try {
                        UUID.fromString(it)
                    } catch (e: Exception) {
                        null
                    }
                }?.getPlayer() ?: Component.translatable("tim_core.bits.someone_else")
            )
    }

    init {
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.HIGHEST, ExpAllHandler::handle)
        CobblemonEvents.POKEMON_CATCH_RATE.subscribe(Priority.LOWEST, PreventQuickBallSpam::handle)
        CobblemonEvents.THROWN_POKEBALL_HIT.subscribe(Priority.NORMAL, PokeballHitReserved::handle)
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.HIGHEST, FishingWithoutATeamCanceller::handle)
        TimCoreEvents.POKEMON_ENTITY_DID_SPAWN.subscribe(Priority.HIGHEST, AttachBucket::handle)
        TimCoreEvents.POKEMON_ENTITY_DID_SPAWN.subscribe(Priority.HIGHEST, AttachSpawnCause::handle)

        PlayerSpawnerFactory.influenceBuilders.add { PreventSpawnsInfluence() }
        PlayerSpawnerFactory.influenceBuilders.add { EntityDidSpawn() }
        PlatformEvents.SERVER_STARTED.subscribe(Priority.LOWEST) {
            fishingSpawner.influences.add(PreventSpawnsInfluence())
            fishingSpawner.influences.add(EntityDidSpawn())
        }
        TimCore.RELOAD_CONFIG.subscribe {
            config._spawnBlacklistMatcher = null
            config._spawnWhitelistMatcher = null
            TimCore.debugger.debug("Cleared the prevent spawns matcher cache due to mod reload.")
        }
    }
}