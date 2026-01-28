package us.timinc.mc.cobblemon.timcore.codec

import com.mojang.serialization.Codec

@Suppress("Unused")
val INT_RANGE_CODEC: Codec<IntRange> = Codec.STRING.xmap(
    { str ->
        val (start, end) = str.split("..")

        try {
            val actualStart = when (start.lowercase()) {
                "min" -> Int.MIN_VALUE
                else -> start.toInt()
            }
            val actualEnd = when (end.lowercase()) {
                "max" -> Int.MAX_VALUE
                else -> end.toInt()
            }
            actualStart..actualEnd
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("'$start' and/or '$end' is/are not integers", e)
        }
    },
    { it.toString() }
)