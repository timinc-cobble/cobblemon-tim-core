package us.timinc.mc.cobblemon.timcore.matcher

import com.cobblemon.mod.common.util.splitMap
import us.timinc.mc.cobblemon.timcore.TimCore

class MatcherRawData(
    rawString: String,
) {
    var keyPairs: MutableList<Pair<String, String?>> = rawString.splitMap(" ", "=")

    private fun getMatchedKeyPair(
        labels: Set<String>,
    ): Pair<String, String?>? {
        return keyPairs.findLast { it.first in labels }
    }

    fun asString(): String = keyPairs.joinToString(" ") { "${it.first}=${it.second}" }

    fun updateValue(newValue: String, labels: Set<String>) {
        keyPairs = keyPairs.map { (k, v) -> if (k in labels) k to newValue else k to v }.toMutableList()
    }

    fun getString(labels: Set<String>): String? {
        val matchingKeyPair = getMatchedKeyPair(labels) ?: return null
        val value = matchingKeyPair.second
        return if (value.isNullOrBlank()) {
            null
        } else {
            value
        }
    }

    fun setString(newValue: String, labels: Set<String>) {
        updateValue(newValue, labels)
    }

    fun getInt(labels: Set<String>): Int? {
        val stringValue = getString(labels) ?: return null
        try {
            return stringValue.toInt()
        } catch (_: NumberFormatException) {
            TimCore.debugger.debug("Attempted to use non-int value of $stringValue.", true)
            return -1
        }
    }

    fun setInt(newValue: Int, labels: Set<String>) {
        setString(newValue.toString(), labels)
    }

    fun getBoolean(labels: Set<String>): Boolean? {
        val matchingKeyPair = getMatchedKeyPair(labels) ?: return null
        return when (matchingKeyPair.second?.lowercase()) {
            null -> true
            "true", "yes" -> true
            "false", "no" -> false
            else -> null
        }
    }

    fun setBoolean(newValue: Boolean, labels: Set<String>) {
        setString(if (newValue) "yes" else "no", labels)
    }

    fun <T> getList(parser: (String) -> T?, labels: Set<String>): List<T>? {
        val stringValue = getString(labels) ?: return null
        return stringValue.split(",").mapNotNull { parser(it) }
    }

    fun setList(newValue: List<*>, labels: Set<String>, separator: String = ",") {
        setString(newValue.joinToString(separator), labels)
    }

    @Suppress("unused")
    fun getStringList(labels: Set<String>) = getList({ it }, labels)

    fun setStringList(newValue: List<String>, labels: Set<String>, separator: String = ",") =
        setList(newValue, labels, separator)

    fun <T> getSet(parser: (String) -> T?, labels: Set<String>): Set<T>? = getList(parser, labels)?.toSet()

    fun setSet(newValue: Set<*>, labels: Set<String>, separator: String = ",") =
        setList(newValue.toList(), labels, separator)

    fun getStringSet(labels: Set<String>) = getSet({ it }, labels)

    fun setStringSet(newValue: Set<String>, labels: Set<String>, separator: String = ",") =
        setSet(newValue, labels, separator)

    fun getStringMap(
        labels: Set<String>,
        delimiter: String = ",",
        assigner: String = "=",
        keyDelimiter: String = ".",
    ): Map<List<String>, String>? {
        val rawMap = getString(labels) ?: return null
        return rawMap.splitMap(delimiter, assigner)
            .fold(mutableMapOf()) { acc, (k, v) ->
                if (v == null) acc else acc.plus(k.split(keyDelimiter) to v).toMutableMap()
            }
    }

    fun setStringMap(
        newValue: Map<List<String>, String>,
        labels: Set<String>,
        delimiter: String,
        assigner: String,
        keyDelimiter: String,
    ) {
        val stringed = newValue.entries.fold(mutableListOf<String>()) { acc, (k, v) ->
            acc.plus("${k.joinToString(keyDelimiter)}$assigner$v").toMutableList()
        }.joinToString(delimiter)
        setString(stringed, labels)
    }

    fun getFloatPairMap(
        labels: Set<String>,
        delimiter: String = ",",
        assigner: String = "=",
        keyDelimiter: String = ".",
        valueDelimiter: String = ":",
    ): Map<List<String>, Pair<Float, Float>>? {
        return getStringMap(labels, delimiter, assigner, keyDelimiter)?.entries
            ?.fold(mutableMapOf()) { acc, (k, v) ->
                try {
                    val split = v.split(valueDelimiter)
                    val min = split[0].toFloat()
                    val max = split[1].toFloat()
                    acc.plus(k to (min to max)).toMutableMap()
                } catch (_: Exception) {
                    TimCore.debugger.debug("Attempted to use non-int-pair value of $v.", true)
                    acc
                }
            }
    }

    fun setFloatPairMap(
        newValue: Map<List<String>, Pair<Float, Float>>,
        labels: Set<String>,
        delimiter: String = ",",
        assigner: String = "=",
        keyDelimiter: String = ".",
        valueDelimiter: String = ":",
    ) {
        val stringed = newValue.entries.fold(mutableListOf<String>()) { acc, (k, v) ->
            acc.plus("${k.joinToString(keyDelimiter)}$assigner${v.first}$valueDelimiter${v.second}").toMutableList()
        }.joinToString(delimiter)
        setString(stringed, labels)
    }
}