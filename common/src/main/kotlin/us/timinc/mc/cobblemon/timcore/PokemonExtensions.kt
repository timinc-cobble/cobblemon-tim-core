package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.pokemon.Pokemon

fun Pokemon.getIdentifier() = "${getDisplayName().string}[${uuid}]"