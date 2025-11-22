package us.timinc.mc.cobblemon.timcore.influence

import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition
import us.timinc.mc.cobblemon.timcore.LimitedList
import us.timinc.mc.cobblemon.timcore.TimCore
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class PreventSpawnsInfluence : SpawningInfluence {
    companion object {
        enum class CacheResult(val value: Boolean) {
            YES(true), NO(false), COMPUTING(false)
        }

        private val cache = ConcurrentHashMap<String, CacheResult>()
        private val detailQueue = LinkedBlockingQueue<Pair<String, PokemonSpawnDetail>>()
        private var runnerThread: Thread? = null

        init {
            TimCore.RELOAD_CONFIG.subscribe {
                cache.clear()
                detailQueue.clear()
                TimCore.debugger.debug("Cleared the prevent spawns cache due to mod reload.")
            }
            startRunner()
        }

        private fun startRunner() {
            runnerThread = thread(name = "PreventSpawnsWorker", isDaemon = true) {
                while (!Thread.currentThread().isInterrupted) {
                    try {
                        val (detailId, spawnDetail) = detailQueue.take()

                        val debugger = TimCore.debugger.getCaseDebugger("PreventSpawns:$detailId")
                        debugger.debug("Processing queued spawn calculation.")

                        val result = try {
                            LimitedList.PokemonMatcherList.matchesList(
                                spawnDetail.pokemon.create(),
                                TimCore.config.spawnWhitelistMatcher,
                                TimCore.config.spawnBlacklistMatcher,
                            )
                        } catch (e: Exception) {
                            debugger.debug("Error processing spawn: ${e.message}")
                            false
                        }

                        cache[detailId] = if (result) CacheResult.YES else CacheResult.NO
                        debugger.debug("Calculated as $result.")
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                        break
                    }
                }
            }
        }
    }

    override fun affectSpawnable(detail: SpawnDetail, spawnablePosition: SpawnablePosition): Boolean {
        if (detail !is PokemonSpawnDetail) return true
        if (TimCore.config.spawnBlacklistMatcher.isEmpty() && TimCore.config.spawnWhitelistMatcher.isEmpty()) return true

        val debugger = TimCore.debugger.getCaseDebugger("PreventSpawns:${detail.id}")

        val cached = cache[detail.id]
        if (cached != null && cached != CacheResult.COMPUTING) {
            return cached.value
        }

        val previous = cache.putIfAbsent(detail.id, CacheResult.COMPUTING)
        if (previous == null) {
            detailQueue.offer(detail.id to detail)
            debugger.debug("No cached value, queued for calculation.")
        }

        return false
    }
}