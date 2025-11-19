package us.timinc.mc.cobblemon.timcore

import io.wispforest.owo.network.OwoNetChannel
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

open class NeoForgeOwoNetwork(id: ResourceLocation) : AbstractOwoNetwork(id) {
    private val mainChannel: OwoNetChannel = OwoNetChannel.create(id)

    override fun <T : Record> sendServerPacket(packet: T) {
        mainChannel.clientHandle().send(packet)
    }

    override fun <T : Record> sendClientPacket(packet: T, player: ServerPlayer) {
        mainChannel.serverHandle(player).send(packet)
    }
}
