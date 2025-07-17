package us.timinc.mc.cobblemon.timcore

import io.wispforest.owo.network.ServerAccess

interface OwoServerNetworkHandler<Packet : Record> {
    val serverPacketClass: Class<Packet>
    fun handleServer(data: Packet, serverAccess: ServerAccess)
}