package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.moves.Move
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.pokemon.IVs
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.getPlayer
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import us.timinc.mc.cobblemon.timcore.data.CustomPropertyExtractorWhitelistManager
import us.timinc.mc.cobblemon.timcore.handler.AttachBucket
import us.timinc.mc.cobblemon.timcore.handler.AttachSpawnCause
import us.timinc.mc.cobblemon.timcore.handler.ExpAllHandler
import us.timinc.mc.cobblemon.timcore.handler.FishingWithoutATeamCanceller
import us.timinc.mc.cobblemon.timcore.handler.PokeballHitReserved
import us.timinc.mc.cobblemon.timcore.handler.PreventQuickBallSpam
import us.timinc.mc.cobblemon.timcore.influence.EntityDidSpawn
import us.timinc.mc.cobblemon.timcore.influence.PreventSpawnsInfluence
import us.timinc.mc.cobblemon.timcore.matcher.piece.CompoundTagFloatRangePiece
import us.timinc.mc.cobblemon.timcore.matcher.piece.CompoundTagPiece
import us.timinc.mc.cobblemon.timcore.matcher.piece.IntRangePiece
import us.timinc.mc.cobblemon.timcore.matcher.piece.PropertiesPiece
import us.timinc.mc.cobblemon.timcore.matcher.piece.StringSetPiece
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
                    } catch (_: Exception) {
                        null
                    }
                }?.getPlayer() ?: Component.translatable("tim_core.bits.someone_else")
            )
    }

    @Suppress("unused")
    object PokemonMatcherPieces {
        val MAX_IVS = PokemonMatcher.registerPiece(
            IntRangePiece(
                getter = { it.ivs.count { (_, iv) -> iv == IVs.MAX_VALUE } },
                minKeys = setOf("max_ivs", "max_ivs_min"),
                maxKeys = setOf("max_ivs_max"),
            )
        )
        val LEVEL = PokemonMatcher.registerPiece(
            IntRangePiece(
                getter = { it.level },
                minKeys = setOf("level_min"),
                maxKeys = setOf("level_max"),
            )
        )
        val TOTAL_IVS = PokemonMatcher.registerPiece(
            IntRangePiece(
                getter = { it.ivs.total() },
                minKeys = setOf("total_ivs_min"),
                maxKeys = setOf("total_ivs_max"),
            )
        )
        val TOTAL_EVS = PokemonMatcher.registerPiece(
            IntRangePiece(
                getter = { it.evs.total() },
                minKeys = setOf("total_evs_min"),
                maxKeys = setOf("total_evs_max"),
            )
        )
        val FRIENDSHIP = PokemonMatcher.registerPiece(
            IntRangePiece(
                getter = { it.friendship },
                minKeys = setOf("friendship_min"),
                maxKeys = setOf("friendship_max"),
            )
        )
        val LABELS = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.form.labels },
                whitelistKeys = setOf("labels"),
                blacklistKeys = setOf("not_labels"),
                matchAnyWhitelistKeys = setOf("any_label"),
            )
        )
        val TYPES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.types.map { type -> type.resourceLocation.toString() }.toSet() },
                whitelistKeys = setOf("types", "elemental_types"),
                blacklistKeys = setOf("not_types"),
                matchAnyWhitelistKeys = setOf("any_type"),
            )
        )
        val PRIMARY_TYPES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.types.toList().getOrNull(0)?.resourceLocation?.toString()?.let(::setOf) ?: emptySet() },
                whitelistKeys = setOf("primary_types"),
                blacklistKeys = setOf("not_primary_types"),
                matchAnyWhitelistBackup = true
            )
        )
        val SECONDARY_TYPES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.types.toList().getOrNull(1)?.resourceLocation?.toString()?.let(::setOf) ?: emptySet() },
                whitelistKeys = setOf("secondary_types"),
                blacklistKeys = setOf("not_secondary_types"),
                matchAnyWhitelistBackup = true,
            )
        )
        val EGG_GROUPS = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.form.eggGroups.map { eg -> eg.name }.toSet() },
                whitelistKeys = setOf("egg_groups"),
                blacklistKeys = setOf("not_egg_groups"),
                matchAnyWhitelistBackup = true,
            )
        )
        val ABILITIES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.ability.template.name.let(::setOf) },
                whitelistKeys = setOf("abilities"),
                blacklistKeys = setOf("not_abilities"),
                matchAnyWhitelistBackup = true,
            )
        )
        val BUCKETS = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.getBucket()?.let(::setOf) ?: emptySet() },
                whitelistKeys = setOf("buckets"),
                blacklistKeys = setOf("not_buckets"),
                matchAnyWhitelistBackup = true,
            )
        )
        val FORMS = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.form.name.let(::setOf) },
                whitelistKeys = setOf("forms"),
                blacklistKeys = setOf("not_forms"),
                matchAnyWhitelistBackup = true,
            )
        )
        val ASPECTS = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.aspects },
                whitelistKeys = setOf("aspects"),
                blacklistKeys = setOf("not_aspects"),
                matchAnyWhitelistKeys = setOf("any_aspects"),
            )
        )
        val MOVES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.moveSet.getMoves().map(Move::name).toSet() },
                whitelistKeys = setOf("active_moves"),
                blacklistKeys = setOf("not_active_moves"),
                matchAnyWhitelistKeys = setOf("any_active_moves"),
            )
        )
        val MOVE_TYPES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.moveSet.getMoves().map { move -> move.type.showdownId }.toSet() },
                whitelistKeys = setOf("active_move_types"),
                blacklistKeys = setOf("not_active_move_types"),
                matchAnyWhitelistKeys = setOf("any_active_move_types"),
            )
        )
        val PROPERTIES = PokemonMatcher.registerPiece(PropertiesPiece())
        val PERSISTENT_DATA = PokemonMatcher.registerPiece(
            CompoundTagPiece(
                getter = { it.persistentData },
                propsKeys = setOf("persistent_data"),
                matchAnyPropKeys = setOf("any_persistent_data"),
            )
        )
        val PERSISTENT_DATA_RANGE = PokemonMatcher.registerPiece(
            CompoundTagFloatRangePiece(
                getter = { it.persistentData },
                propsKeys = setOf("persistent_data_range"),
                matchAnyPropKeys = setOf("any_persistent_data_range")
            )
        )
        val DYNAMAX_LEVEL = PokemonMatcher.registerPiece(
            IntRangePiece(
                getter = { it.dmaxLevel },
                minKeys = setOf("dynamax_level_min", "dmax_level_min"),
                maxKeys = setOf("dynamax_level_max", "dmax_level_max"),
            )
        )
        val TERA_TYPES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { setOf(it.teraType.showdownId()) },
                whitelistKeys = setOf("tera_types"),
                blacklistKeys = setOf("not_tera_type"),
                matchAnyWhitelistBackup = true,
            )
        )
        val NICKNAMES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.nickname?.string?.let(::setOf) ?: emptySet() },
                whitelistKeys = setOf("nicknames"),
                blacklistKeys = setOf("not_nicknames"),
                matchAnyWhitelistBackup = true,
            )
        )
        val STATUSES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.status?.status?.showdownName?.let(::setOf) ?: emptySet() },
                whitelistKeys = setOf("statuses"),
                blacklistKeys = setOf("not_statuses"),
                matchAnyWhitelistBackup = true,
            )
        )
        val GENDERS = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.gender.name.let(::setOf) },
                whitelistKeys = setOf("genders"),
                blacklistKeys = setOf("not_genders"),
                matchAnyWhitelistBackup = true,
            )
        )
        val NATURES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.nature.name.toString().let(::setOf) },
                whitelistKeys = setOf("natures"),
                blacklistKeys = setOf("not_natures"),
                matchAnyWhitelistBackup = true,
            )
        )
        val IVS = Stats.BATTLE_ONLY.fold(mutableMapOf<Stat, IntRangePiece>()) { acc, stat ->
            acc.plus(
                stat to (PokemonMatcher.registerPiece(
                    IntRangePiece(
                        getter = { it.ivs[stat] ?: 0 },
                        minKeys = setOf("${stat.showdownId}_iv_min", "${stat.identifier.path}_iv_min"),
                        maxKeys = setOf("${stat.showdownId}_iv_max", "${stat.identifier.path}_iv_max"),
                    )
                ) as IntRangePiece)
            ).toMutableMap()
        }
        val EVS = Stats.BATTLE_ONLY.fold(mutableMapOf<Stat, IntRangePiece>()) { acc, stat ->
            acc.plus(
                stat to (PokemonMatcher.registerPiece(
                    IntRangePiece(
                        getter = { it.evs[stat] ?: 0 },
                        minKeys = setOf("${stat.showdownId}_ev_min", "${stat.identifier.path}_ev_min"),
                        maxKeys = setOf("${stat.showdownId}_ev_max", "${stat.identifier.path}_ev_max"),
                    )
                ) as IntRangePiece)
            ).toMutableMap()
        }
    }

    init {
        PokemonMatcherPieces

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