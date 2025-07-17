package us.timinc.mc.cobblemon.timcore

import io.wispforest.owo.network.ClientAccess

interface OwoClientNetworkHandler<Packet : Record> {
    val clientPacketClass: Class<Packet>
    fun handleClient(data: Packet, clientAccess: ClientAccess)
}