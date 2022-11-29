package com.armemius.magepunk.mixins

import com.armemius.magepunk.registries.NetworkRegistry
import com.armemius.magepunk.util.IPlayerDataManager
import com.armemius.magepunk.util.NetworkingUtil
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Suppress("unused")
@Mixin(ServerPlayerEntity::class)
class ServerPlayerMixin {
    @Inject(method = ["copyFrom"], at = [At("HEAD")])
    fun copyFrom(oldPlayer: ServerPlayerEntity, alive: Boolean, ci: CallbackInfo?) {
        (this as IPlayerDataManager).setPersistentNbtCompound((oldPlayer as IPlayerDataManager).getPersistentData())
        (this as IPlayerDataManager).setTechsNbt((oldPlayer as IPlayerDataManager).getTechsNbt())
        NetworkingUtil.syncTechsS2C(this as ServerPlayerEntity)
    }
}