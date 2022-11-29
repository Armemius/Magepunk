package com.armemius.magepunk.mixins

import com.armemius.magepunk.util.IClientPlayerExt
import com.armemius.magepunk.util.NetworkingUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Suppress("unused")
@Mixin(ClientPlayerEntity::class)
abstract class ClientPlayerMixin: IClientPlayerExt {
    private var isDirty: Boolean = true

    @Inject(method = ["tick"], at = [At("HEAD")])
    fun injectTick(ci: CallbackInfo?) {
        if (isDirty) {
            NetworkingUtil.requestTechsSyncC2S()
        }
    }

    override fun markDirty() {
        isDirty = true
    }

    override fun markClean() {
        isDirty = false
    }
}