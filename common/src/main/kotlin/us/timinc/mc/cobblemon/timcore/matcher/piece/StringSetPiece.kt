package us.timinc.mc.cobblemon.timcore.matcher.piece

import com.cobblemon.mod.common.pokemon.Pokemon
import us.timinc.mc.cobblemon.timcore.PokemonMatcher
import us.timinc.mc.cobblemon.timcore.condition.SetCondition
import us.timinc.mc.cobblemon.timcore.matcher.MatcherRawData

class StringSetPiece(
    val getter: (pokemon: Pokemon) -> Set<String>,
    val whitelistKeys: Set<String>? = null,
    val whitelistBackup: Set<String> = emptySet(),
    val blacklistKeys: Set<String>? = null,
    val blacklistBackup: Set<String> = emptySet(),
    val matchAnyWhitelistKeys: Set<String>? = null,
    val matchAnyWhitelistBackup: Boolean = false,
) : Piece<SetCondition<Pokemon, String>> {
    override fun condition(raw: MatcherRawData): SetCondition<Pokemon, String> = SetCondition(
        whitelistKeys?.let(raw::getStringSet) ?: whitelistBackup,
        blacklistKeys?.let(raw::getStringSet) ?: blacklistBackup,
        getter,
        matchAnyWhitelistKeys?.let(raw::getBoolean) ?: matchAnyWhitelistBackup
    )

    fun apply(
        matcher: PokemonMatcher,
        whitelist: Set<String>? = null,
        blacklist: Set<String>? = null,
        matchAnyWhitelist: Boolean? = null,
    ) {
        whitelist?.let { whitelist ->
            whitelistKeys?.let { whitelistKeys ->
                matcher.raw.setStringSet(
                    whitelist,
                    whitelistKeys
                )
            }
        }
        blacklist?.let { blacklist ->
            blacklistKeys?.let { blacklistKeys ->
                matcher.raw.setStringSet(
                    blacklist,
                    blacklistKeys
                )
            }
        }
        matchAnyWhitelist?.let { matchAnyWhitelist ->
            matchAnyWhitelistKeys?.let { matchAnyWhitelistKeys ->
                matcher.raw.setBoolean(matchAnyWhitelist, matchAnyWhitelistKeys)
            }
        }
    }

    override val allKeys: Set<String>
        get() = (whitelistKeys ?: emptySet()) + (blacklistKeys ?: emptySet()) + (matchAnyWhitelistKeys
            ?: emptySet())
}