package us.timinc.mc.cobblemon.timcore.influence

import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import us.timinc.mc.cobblemon.timcore.LimitedList
import us.timinc.mc.cobblemon.timcore.TimCore

class PreventSpawnsInfluence : SpawningInfluence {
    companion object {
        val cache: MutableMap<String, Boolean> = mutableMapOf()

        init {
            TimCore.RELOAD_CONFIG.subscribe {
                cache.clear()
                TimCore.debugger.debug("Cleared the prevent spawns cache due to mod reload.")
            }
        }
    }

    override fun affectSpawnable(detail: SpawnDetail, ctx: SpawningContext): Boolean {
        if (detail !is PokemonSpawnDetail) return true
        return cache[detail.id] ?: run {
            cache[detail.id] = LimitedList.PokemonMatcherList.matchesList(
                detail.pokemon.create(),
                TimCore.config.spawnWhitelistMatcher,
                TimCore.config.spawnBlacklistMatcher,
            )
            TimCore.debugger.debug("Cached spawn ${detail.id} as ${cache[detail.id]} due to spawning blacklist/whitelist.")
            return cache[detail.id]!!
        }
    }
}