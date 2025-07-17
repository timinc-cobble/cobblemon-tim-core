package us.timinc.mc.cobblemon.timcore

import net.minecraft.server.level.ServerPlayer
import java.util.*

@Suppress("unused")
class Holder<T> {
    class Receipt<T>(
        val player: ServerPlayer,
        val data: T,
    )

    interface ReceiptPacketMaker<T : Record> {
        fun toPacket(id: UUID): T
    }

    private val receipts: MutableMap<UUID, Receipt<T>> = mutableMapOf()

    fun hangReceipt(player: ServerPlayer, data: T): UUID {
        val id: UUID = UUID.randomUUID()
        receipts[id] = Receipt(player, data)
        return id
    }

    fun pullReceipt(id: UUID, accessingPlayer: ServerPlayer): Receipt<T> {
        val receipt: Receipt<T> =
            receipts[id] ?: throw Error("Player $accessingPlayer attempted to access non-existent receipt $id")
        if (receipt.player != accessingPlayer) throw Error("Player $accessingPlayer attempted to access someone else's receipt $id")
        receipts.remove(id)
        return receipt
    }
}