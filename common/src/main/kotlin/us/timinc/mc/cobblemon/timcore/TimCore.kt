package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.mark.Mark
import com.cobblemon.mod.common.api.moves.Move
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.riding.RidingStyle
import com.cobblemon.mod.common.api.riding.stats.RidingStat
import com.cobblemon.mod.common.pokemon.IVs
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.getPlayer
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import us.timinc.mc.cobblemon.timcore.command.PokemonMatcherTestCommand
import us.timinc.mc.cobblemon.timcore.data.CustomPropertyExtractorWhitelistManager
import us.timinc.mc.cobblemon.timcore.handler.AttachBucket
import us.timinc.mc.cobblemon.timcore.handler.AttachSpawnCause
import us.timinc.mc.cobblemon.timcore.handler.DisableEvGain
import us.timinc.mc.cobblemon.timcore.handler.ExpAllHandler
import us.timinc.mc.cobblemon.timcore.handler.FishingWithoutATeamCanceller
import us.timinc.mc.cobblemon.timcore.handler.PokeballHitReserved
import us.timinc.mc.cobblemon.timcore.handler.PreventQuickBallSpam
import us.timinc.mc.cobblemon.timcore.influence.EntityDidSpawn
import us.timinc.mc.cobblemon.timcore.influence.PreventSpawnsInfluence
import us.timinc.mc.cobblemon.timcore.matcher.piece.BooleanPiece
import us.timinc.mc.cobblemon.timcore.matcher.piece.CompoundTagFloatRangePiece
import us.timinc.mc.cobblemon.timcore.matcher.piece.CompoundTagPiece
import us.timinc.mc.cobblemon.timcore.matcher.piece.DoubleRangePiece
import us.timinc.mc.cobblemon.timcore.matcher.piece.FloatRangePiece
import us.timinc.mc.cobblemon.timcore.matcher.piece.IntRangePiece
import us.timinc.mc.cobblemon.timcore.matcher.piece.ItemTagPiece
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
        val disableEvGain: Boolean = false
        val bucketKeys: Map<String, String> = mapOf(
            "common" to "tim_core.buckets.common",
            "uncommon" to "tim_core.buckets.uncommon",
            "rare" to "tim_core.buckets.rare",
            "ultra-rare" to "tim_core.buckets.ultra_rare"
        )
        val unknownBucketKey: String = "tim_core.buckets.unknown"
        val successKey: String = "tim_core.result.success"
        val failureKey: String = "tim_core.result.failure"

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
                getter = Pokemon::level,
                minKeys = setOf("level_min", "lvl_min"),
                maxKeys = setOf("level_max", "lvl_max"),
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
                getter = Pokemon::friendship,
                minKeys = setOf("friendship_min", "happiness_min"),
                maxKeys = setOf("friendship_max", "happiness_max"),
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
                getter = { it.types.map { type -> type.showdownId }.toSet() },
                whitelistKeys = setOf("types", "elemental_types"),
                blacklistKeys = setOf("not_types"),
                matchAnyWhitelistKeys = setOf("any_type"),
            )
        )
        val PRIMARY_TYPES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.primaryType.showdownId.let(::setOf) },
                whitelistKeys = setOf("primary_types"),
                blacklistKeys = setOf("not_primary_types"),
                matchAnyWhitelistBackup = true
            )
        )
        val SECONDARY_TYPES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.secondaryType?.showdownId?.let(::setOf) ?: emptySet() },
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
                getter = Pokemon::aspects,
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
        val TOTAL_POWER_POINTS = PokemonMatcher.registerPiece(
            IntRangePiece(
                getter = { it.moveSet.getMoves().fold(0) { acc, move -> acc + move.currentPp } },
                minKeys = setOf("total_pp_min", "pp_min", "power_points_min", "sum_pp_min"),
                maxKeys = setOf("total_pp_max", "pp_max", "power_points_max", "sum_pp_max"),
            )
        )
        val PROPERTIES = PokemonMatcher.registerPiece(PropertiesPiece())
        val PERSISTENT_DATA = PokemonMatcher.registerPiece(
            CompoundTagPiece(
                getter = Pokemon::persistentData,
                propsKeys = setOf("persistent_data"),
                matchAnyPropKeys = setOf("any_persistent_data"),
            )
        )
        val PERSISTENT_DATA_RANGE = PokemonMatcher.registerPiece(
            CompoundTagFloatRangePiece(
                getter = Pokemon::persistentData,
                propsKeys = setOf("persistent_data_range"),
                matchAnyPropKeys = setOf("any_persistent_data_range")
            )
        )
        val DYNAMAX_LEVEL = PokemonMatcher.registerPiece(
            IntRangePiece(
                getter = Pokemon::dmaxLevel,
                minKeys = setOf("dynamax_level_min", "dmax_level_min", "dynamax_lvl_min", "dmax_lvl_min"),
                maxKeys = setOf("dynamax_level_max", "dmax_level_max", "dynamax_lvl_max", "dmax_lvl_max"),
            )
        )
        val TERA_TYPES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { setOf(it.teraType.showdownId()) },
                whitelistKeys = setOf("tera_types"),
                blacklistKeys = setOf("not_tera_types"),
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
                whitelistKeys = setOf("genders", "sexes"),
                blacklistKeys = setOf("not_genders", "not_sexes"),
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
        val IVS = Stats.PERMANENT.fold(mutableMapOf<Stat, IntRangePiece>()) { acc, stat ->
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
        val EVS = Stats.PERMANENT.fold(mutableMapOf<Stat, IntRangePiece>()) { acc, stat ->
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
        val EV_YIELDS = Stats.PERMANENT.fold(mutableMapOf<Stat, IntRangePiece>()) { acc, stat ->
            acc.plus(
                stat to (PokemonMatcher.registerPiece(
                    IntRangePiece(
                        getter = { it.form.evYield[stat] ?: 0 },
                        minKeys = setOf("${stat.showdownId}_ev_yield_min", "${stat.identifier.path}_ev_yield_min"),
                        maxKeys = setOf("${stat.showdownId}_ev_yield_max", "${stat.identifier.path}_ev_yield_max"),
                    )
                ) as IntRangePiece)
            ).toMutableMap()
        }
        val STATS = Stats.PERMANENT.map { stat ->
            PokemonMatcher.registerPiece(
                IntRangePiece(
                    getter = { it.getStat(stat) },
                    minKeys = setOf("${stat.showdownId}_min", "${stat.identifier.path}_min"),
                    maxKeys = setOf("${stat.showdownId}_max", "${stat.identifier.path}_max"),
                )
            )
        }
        val MOVE_MIN_ACCURACY = PokemonMatcher.registerPiece(
            DoubleRangePiece(
                getter = { it.moveSet.getMoves().minByOrNull(Move::accuracy)?.accuracy ?: 0.0 },
                minKeys = setOf("move_min_accuracy_min", "min_accuracy_min", "move_min_acc_min", "min_acc_min"),
                maxKeys = setOf("move_min_accuracy_max", "min_accuracy_max", "move_min_acc_max", "min_acc_max"),
            )
        )
        val MOVE_MAX_ACCURACY = PokemonMatcher.registerPiece(
            DoubleRangePiece(
                getter = { it.moveSet.getMoves().maxByOrNull(Move::accuracy)?.accuracy ?: 0.0 },
                minKeys = setOf("move_max_accuracy_min", "max_accuracy_min", "move_max_acc_min", "max_acc_min"),
                maxKeys = setOf("move_max_accuracy_max", "max_accuracy_max", "move_max_acc_max", "max_acc_max"),
            )
        )
        val MOVE_MIN_POWER = PokemonMatcher.registerPiece(
            DoubleRangePiece(
                getter = { it.moveSet.getMoves().minByOrNull(Move::power)?.power ?: 0.0 },
                minKeys = setOf("move_min_power_min", "min_power_min", "move_min_pow_min", "min_pow_min"),
                maxKeys = setOf("move_min_power_max", "min_power_max", "move_min_pow_max", "min_pow_max"),
            )
        )
        val MOVE_MAX_POWER = PokemonMatcher.registerPiece(
            DoubleRangePiece(
                getter = { it.moveSet.getMoves().maxByOrNull(Move::power)?.power ?: 0.0 },
                minKeys = setOf("move_max_power_min", "max_power_min", "move_max_pow_min", "max_pow_min"),
                maxKeys = setOf("move_max_power_max", "max_power_max", "move_max_pow_max", "max_pow_max"),
            )
        )
        val MARKINGS = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.markings.map(Int::toString).toSet() },
                whitelistKeys = setOf("markings"),
                blacklistKeys = setOf("not_markings"),
                matchAnyWhitelistKeys = setOf("any_markings")
            )
        )
        val MOVE_COUNT = PokemonMatcher.registerPiece(
            IntRangePiece(
                getter = { it.moveSet.getMoves().size },
                minKeys = setOf("move_count_min", "moves_min"),
                maxKeys = setOf("move_count_max", "moves_max"),
            )
        )
        val CURRENT_FULLNESS = PokemonMatcher.registerPiece(
            IntRangePiece(
                getter = Pokemon::currentFullness,
                minKeys = setOf("current_fullness_min", "cur_fullness_min", "current_full_min", "cur_full_min"),
                maxKeys = setOf("current_fullness_max", "cur_fullness_max", "current_full_max", "cur_full_max"),
            )
        )
        val MAX_FULLNESS = PokemonMatcher.registerPiece(
            IntRangePiece(
                getter = Pokemon::getMaxFullness,
                minKeys = setOf("max_fullness_min", "max_full_min"),
                maxKeys = setOf("max_fullness_max", "max_full_max"),
            )
        )
        val CURRENT_HEALTH = PokemonMatcher.registerPiece(
            IntRangePiece(
                getter = Pokemon::currentHealth,
                minKeys = setOf("current_health_min", "cur_health_min", "current_hp_min", "cur_hp_min"),
                maxKeys = setOf("current_health_max", "cur_health_max", "current_hp_max", "cur_hp_max"),
            )
        )
        val MAX_HEALTH = PokemonMatcher.registerPiece(
            IntRangePiece(
                getter = Pokemon::maxHealth,
                minKeys = setOf("max_health_min", "max_hp_min"),
                maxKeys = setOf("max_health_max", "max_hp_max"),
            )
        )
        val MARKS = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = {
                    it.marks.map { mark -> mark.identifier.toString() }.toSet() + it.marks.map(Mark::name).toSet()
                },
                whitelistKeys = setOf("marks"),
                blacklistKeys = setOf("not_marks"),
                matchAnyWhitelistKeys = setOf("any_marks")
            )
        )
        val ACTIVE_MARKS = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.activeMark?.let { mark -> setOf(mark.name, mark.identifier.toString()) } ?: emptySet() },
                whitelistKeys = setOf("active_marks"),
                blacklistKeys = setOf("not_active_marks"),
                matchAnyWhitelistKeys = setOf("any_active_marks"),
            )
        )
        val EXPERIENCE = PokemonMatcher.registerPiece(
            IntRangePiece(
                getter = Pokemon::experience,
                minKeys = setOf("experience_min", "exp_min"),
                maxKeys = setOf("experience_max", "exp_max"),
            )
        )
        val CAUGHT_BALLS = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.caughtBall.let { ball -> setOf(ball.name.toString(), ball.name.path) } },
                whitelistKeys = setOf("caught_balls"),
                blacklistKeys = setOf("not_caught_balls"),
                matchAnyWhitelistKeys = setOf("any_caught_balls"),
            )
        )
        val EFFECTIVE_NATURES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.effectiveNature.let { nature -> setOf(nature.name.toString(), nature.name.path) } },
                whitelistKeys = setOf("effective_natures"),
                blacklistKeys = setOf("not_effective_natures"),
                matchAnyWhitelistKeys = setOf("any_effective_natures"),
            )
        )
        val MINTED_NATURES = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = {
                    it.mintedNature?.let { nature -> setOf(nature.name.toString(), nature.name.path) } ?: emptySet()
                },
                whitelistKeys = setOf("minted_natures"),
                blacklistKeys = setOf("not_minted_natures"),
                matchAnyWhitelistKeys = setOf("any_minted_natures"),
            )
        )
        val EXPERIENCE_GROUPS = PokemonMatcher.registerPiece(
            StringSetPiece(
                getter = { it.experienceGroup.name.let(::setOf) },
                whitelistKeys = setOf("experience_groups", "exp_groups"),
                blacklistKeys = setOf("not_experience_groups", "not_exp_groups"),
                matchAnyWhitelistKeys = setOf("any_experience_groups", "any_exp_groups"),
            )
        )
        val RIDE_STAMINA = PokemonMatcher.registerPiece(
            FloatRangePiece(
                getter = Pokemon::rideStamina,
                minKeys = setOf("ride_stamina_min", "stamina_min", "stam_min"),
                maxKeys = setOf("ride_stamina_max", "stamina_max", "stam_max"),
            )
        )
        val RIDE_STATS = RidingStyle.entries.map { style ->
            RidingStat.entries.map { stat ->
                PokemonMatcher.registerPiece(
                    FloatRangePiece(
                        getter = { it.getRideStat(style, stat) },
                        minKeys = setOf("${style.name.lowercase()}_${stat.name.lowercase()}_min"),
                        maxKeys = setOf("${style.name.lowercase()}_${stat.name.lowercase()}_max"),
                    )
                )
            }
        }
        val HAS_HELD_ITEM = PokemonMatcher.registerPiece(
            BooleanPiece(
                { !it.heldItem().isEmpty },
                setOf("has_held_item")
            )
        )
        val HELD_ITEM_TAG = PokemonMatcher.registerPiece(
            ItemTagPiece(
                { it.heldItem() },
                setOf("held_item_tag", "held_tag")
            )
        )
        val HAS_COSMETIC_ITEM = PokemonMatcher.registerPiece(
            BooleanPiece(
                { !it.cosmeticItem().isEmpty },
                setOf("has_cosmetic_item")
            )
        )
        val COSMETIC_ITEM_TAG = PokemonMatcher.registerPiece(
            ItemTagPiece(
                { it.cosmeticItem() },
                setOf("cosmetic_item_tag", "cosmetic_tag")
            )
        )
    }

    init {
        PokemonMatcherPieces

        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.HIGHEST, ExpAllHandler::handle)
        CobblemonEvents.POKEMON_CATCH_RATE.subscribe(Priority.LOWEST, PreventQuickBallSpam::handle)
        CobblemonEvents.THROWN_POKEBALL_HIT.subscribe(Priority.NORMAL, PokeballHitReserved::handle)
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.HIGHEST, FishingWithoutATeamCanceller::handle)
        TimCoreEvents.POKEMON_ENTITY_DID_SPAWN.subscribe(Priority.HIGHEST, AttachBucket::handle)
        TimCoreEvents.POKEMON_ENTITY_DID_SPAWN.subscribe(Priority.HIGHEST, AttachSpawnCause::handle)
        CobblemonEvents.EV_GAINED_EVENT_PRE.subscribe(Priority.NORMAL, DisableEvGain::handle)

        registerGeneralSpawnerInfluence(PreventSpawnsInfluence())
        registerGeneralSpawnerInfluence(EntityDidSpawn())
        registerReloadListener(CustomPropertyExtractorWhitelistManager)
        registerCommand(PokemonMatcherTestCommand)
        RELOAD_CONFIG.subscribe {
            config._spawnBlacklistMatcher = null
            config._spawnWhitelistMatcher = null
            debugger.debug("Cleared the prevent spawns matcher cache due to mod reload.")
        }
    }
}