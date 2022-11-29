package com.armemius.magepunk.util

import net.minecraft.nbt.NbtCompound

interface IPlayerDataManager {
    fun setPersistentNbtCompound(nbt: NbtCompound)
    // This NBT-tag doesn't reset after player's death or whatever
    fun getPersistentData(): NbtCompound
    // This NBT-tag resets
    fun getData(): NbtCompound

    // Used only for research system, doesn't reset
    fun getTechsNbt(): NbtCompound
    fun setTechsNbt(nbt: NbtCompound)
    fun resetTechsNbt()
}