package us.timinc.mc.cobblemon.timcore

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*

open class Debugger<T : AbstractConfig>(
    @Suppress("MemberVisibilityCanBePrivate") val id: String,
    @Suppress("MemberVisibilityCanBePrivate") val config: T,
) {
    private val logger: Logger = LogManager.getLogger(id)

    open fun debug(msg: String, overrideConfig: Boolean = false) {
        if (!config.debug && !overrideConfig) return
        logger.info(msg)
    }

    fun getCaseDebugger(caseUuid: String = UUID.randomUUID().toString()): Case<T> {
        return Case(caseUuid, id, config)
    }

    class Case<T : AbstractConfig>(private val caseUuid: String, id: String, config: T) : Debugger<T>(id, config) {
        override fun debug(msg: String, overrideConfig: Boolean) {
            super.debug("[$caseUuid] $msg", overrideConfig)
        }
    }
}