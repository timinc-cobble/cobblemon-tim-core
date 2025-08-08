package us.timinc.mc.cobblemon.timcore

import net.minecraft.nbt.CompoundTag

fun CompoundTag.getUUIDOrNull(key: String) = if (this.contains(key)) this.getUUID(key) else null
fun CompoundTag.getByteOrNull(key: String) = if (this.contains(key)) this.getByte(key) else null
fun CompoundTag.getShortOrNull(key: String) = if (this.contains(key)) this.getShort(key) else null
fun CompoundTag.getIntOrNull(key: String) = if (this.contains(key)) this.getInt(key) else null
fun CompoundTag.getLongOrNull(key: String) = if (this.contains(key)) this.getLong(key) else null
fun CompoundTag.getFloatOrNull(key: String) = if (this.contains(key)) this.getFloat(key) else null
fun CompoundTag.getDoubleOrNull(key: String) = if (this.contains(key)) this.getDouble(key) else null
fun CompoundTag.getStringOrNull(key: String) = if (this.contains(key)) this.getString(key) else null
fun CompoundTag.getCompoundOrNull(key: String) = if (this.contains(key)) this.getCompound(key) else null
fun CompoundTag.getListOrNull(key: String, i: Int) = if (this.contains(key)) this.getList(key, i) else null
fun CompoundTag.getBooleanOrNull(key: String) = if (this.contains(key)) this.getBoolean(key) else null
fun CompoundTag.getOrNull(key: String) = if (this.contains(key)) this.get(key) else null