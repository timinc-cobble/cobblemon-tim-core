package us.timinc.mc.cobblemon.timcore

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

abstract class AbstractOwoNetwork(val id: ResourceLocation) {
    companion object {
        abstract class ServerNetworkHandler<T : Any> {
            abstract val packetClass: Class<T>
            abstract fun handle(packet: T, serverAccess: Any)
        }

        abstract class ClientNetworkHandler<T : Any> {
            abstract fun handle(data: T, clientAccess: Any)
            abstract val packetClass: Class<T>
        }
    }

    abstract fun <T : Record> sendServerPacket(packet: T)
    abstract fun <T : Record> sendClientPacket(packet: T, player: ServerPlayer)
}