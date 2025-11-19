package us.timinc.mc.cobblemon.timcore

interface OwoServerNetworkHandler<Packet : Record> {
    val serverPacketClass: Class<Packet>
    fun handleServer(data: Packet, serverAccess: Any)
}