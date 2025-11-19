package us.timinc.mc.cobblemon.timcore

interface OwoClientNetworkHandler<Packet : Record> {
    val clientPacketClass: Class<Packet>
    fun handleClient(data: Packet, clientAccess: Any)
}