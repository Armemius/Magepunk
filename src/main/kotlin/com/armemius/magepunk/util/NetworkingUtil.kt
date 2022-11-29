package com.armemius.magepunk.util

import com.armemius.magepunk.registries.NetworkRegistry
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object NetworkingUtil {
    fun syncTechsS2C(player: ServerPlayerEntity) {
        var buf = PacketByteBufs.create()
        buf.writeNbt((player as IPlayerDataManager).getTechsNbt())
        ServerPlayNetworking.send(player, NetworkRegistry.UPDATE_TECHS, buf)
    }

    fun requestTechsSyncC2S() {
        ClientPlayNetworking.send(NetworkRegistry.UPDATE_TECHS, PacketByteBufs.empty())
    }
}