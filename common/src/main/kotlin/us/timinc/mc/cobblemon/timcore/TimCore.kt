package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.getPlayer
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import us.timinc.mc.cobblemon.timcore.data.CustomPropertyExtractorWhitelistManager
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
        val fossilMachineResurrectionTime: Int = 14400
        val bucketKeys: Map<String, String> = mapOf(
            "common" to "tim_core.buckets.common",
            "uncommon" to "tim_core.buckets.uncommon",
            "rare" to "tim_core.buckets.rare",
            "ultra-rare" to "tim_core.buckets.ultra_rare"
        )
        val unknownBucketKey: String = "tim_core.buckets.unknown"

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

    fun getBucketKeyFromName(name: String?) = config.bucketKeys[name] ?: config.unknownBucketKey

    object Tags {
        @JvmField
        val EXP_ALL: TagKey<Item> = TagKey.create(Registries.ITEM, modResource("exp_all"))
    }

    object DataKeys {
        private fun createKey(name: String, namespace: String? = MOD_ID) = "$namespace:$name"

        val SPAWNED_IN_BUCKET = createKey("spawned_in_bucket")
        val ALREADY_HIT_WITH_QUICK_BALL = createKey("already_hit_with_quick_ball")
        val RESERVED_FOR = createKey("reserved_for")
        val SPAWNED_VIA = createKey("spawned_via")
        val SPAWNED_ON = createKey("spawned_on")

        object SpawnerTypes {
            val PLAYER = createKey("spawner_player")
            val FISHING = createKey("spawner_fishing")
            val SNACK = createKey("spawner_snack")
            val UNKNOWN = createKey("spawner_unknown")
        }
    }

    object CustomPokemonProperties {
        val RESERVED_FOR = CustomStringProperty(DataKeys.RESERVED_FOR)
        val SPAWNED_VIA = CustomStringProperty(DataKeys.SPAWNED_VIA)
        val SPAWNED_ON = CustomStringProperty(DataKeys.SPAWNED_ON)
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

        registerGeneralSpawnerInfluence(PreventSpawnsInfluence())
        registerGeneralSpawnerInfluence(EntityDidSpawn())
        registerReloadListener(CustomPropertyExtractorWhitelistManager)
        RELOAD_CONFIG.subscribe {
            config._spawnBlacklistMatcher = null
            config._spawnWhitelistMatcher = null
            debugger.debug("Cleared the prevent spawns matcher cache due to mod reload.")
        }
    }
}