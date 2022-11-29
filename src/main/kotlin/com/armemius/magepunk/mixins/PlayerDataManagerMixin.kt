package com.armemius.magepunk.mixins

import com.armemius.magepunk.util.IPlayerDataManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@SuppressWarnings("unused")
@Mixin(PlayerEntity::class)
abstract class PlayerDataManagerMixin : IPlayerDataManager {
    private var persistentData: NbtCompound? = null
    private var researchData: NbtCompound? = null
    private var data: NbtCompound? = null


    override fun getPersistentData(): NbtCompound {
        if (persistentData == null) {
            persistentData = NbtCompound()
        }
        return persistentData as NbtCompound
    }

    override fun setPersistentNbtCompound(nbt: NbtCompound) {
        persistentData = nbt
    }

    override fun getTechsNbt(): NbtCompound {
        if (researchData == null) {
            researchData = NbtCompound()
        }
        return researchData as NbtCompound
    }

    override fun setTechsNbt(nbt: NbtCompound) {
        researchData = nbt
    }

    override fun resetTechsNbt() {
        researchData = null
    }

    override fun getData(): NbtCompound {
        if (data == null) {
            data = NbtCompound()
        }
        return data as NbtCompound
    }

    @Inject(method = ["writeCustomDataToNbt"], at = [At("HEAD")])
    fun injectWrite(nbt: NbtCompound, ci: CallbackInfo?) {
        if (persistentData != null) {
            nbt.put("mgp.player.persistent_data", persistentData)
        }
        if (data != null) {
            nbt.put("mgp.player.data", data)
        }
        if (researchData != null) {
            nbt.put("mgp.player.research_data", researchData)
        }
    }

    @Inject(method = ["readCustomDataFromNbt"], at = [At("HEAD")])
    fun injectRead(nbt: NbtCompound, ci: CallbackInfo?) {
        if (nbt.contains("mgp.player.persistent_data")) {
            persistentData = nbt.getCompound("mgp.player.persistent_data")
        }
        if (nbt.contains("mgp.player.data")) {
            data = nbt.getCompound("mgp.player.data")
        }
        if (nbt.contains("mgp.player.research_data")) {
            researchData = nbt.getCompound("mgp.player.research_data")
        }
    }
}