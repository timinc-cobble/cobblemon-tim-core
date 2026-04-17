package us.timinc.mc.cobblemon.timcore.handler

import com.cobblemon.mod.common.api.events.pokemon.EvGainedEvent
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCore

object DisableEvGain : AbstractHandler<EvGainedEvent.Pre>() {
    override fun handle(evt: EvGainedEvent.Pre) {
        if (TimCore.config.disableEvGain) evt.cancel()
    }
}