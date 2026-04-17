package us.timinc.mc.cobblemon.timcore.codec

import com.mojang.serialization.Codec

@Suppress("Unused")
val FLOAT_RANGE_CODEC: Codec<ClosedFloatingPointRange<Float>> = Codec.STRING.xmap(
    { str ->
        val (start, end) = str.split("..")

        try {
            val actualStart = when (start.lowercase()) {
                "min" -> Float.MIN_VALUE
                else -> start.toFloat()
            }
            val actualEnd = when (end.lowercase()) {
                "max" -> Float.MAX_VALUE
                else -> end.toFloat()
            }
            actualStart..actualEnd
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("'$start' and/or '$end' is/are not floats", e)
        }
    },
    { it.toString() }
)