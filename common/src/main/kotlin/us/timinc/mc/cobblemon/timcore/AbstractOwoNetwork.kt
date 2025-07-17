package us.timinc.mc.cobblemon.timcore

import io.wispforest.owo.network.ClientAccess
import io.wispforest.owo.network.OwoNetChannel
import io.wispforest.owo.network.ServerAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

abstract class AbstractOwoNetwork(id: ResourceLocation) {
    companion object {
        abstract class ServerNetworkHandler<T : Any> {
            abstract val packetClass: Class<T>
            abstract fun handle(packet: T, serverAccess: ServerAccess)
        }

        abstract class ClientNetworkHandler<T : Any> {
            abstract fun handle(data: T, clientAccess: ClientAccess)
            abstract val packetClass: Class<T>
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val mainChannel: OwoNetChannel = OwoNetChannel.create(id)

    fun <T : Record> sendServerPacket(packet: T, channel: OwoNetChannel = mainChannel) {
        channel.clientHandle().send(packet)
    }

    fun <T : Record> sendClientPacket(packet: T, player: ServerPlayer, channel: OwoNetChannel = mainChannel) {
        channel.serverHandle(player).send(packet)
    }
}