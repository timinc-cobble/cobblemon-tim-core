package us.timinc.mc.cobblemon.timcore

interface OwoReceipt<PacketMaker : Holder.ReceiptPacketMaker<ClientBound>, ClientBound : Record, ServerBound : Record> :
    OwoClientNetworkHandler<ClientBound>, OwoServerNetworkHandler<ServerBound>