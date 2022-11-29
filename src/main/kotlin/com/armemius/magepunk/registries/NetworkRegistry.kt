package com.armemius.magepunk.registries
import com.armemius.magepunk.Magepunk
import com.armemius.magepunk.mixins.PlayerDataManagerMixin
import com.armemius.magepunk.util.IClientPlayerExt
import com.armemius.magepunk.util.IPlayerDataManager
import com.armemius.magepunk.util.NetworkingUtil
import com.armemius.magepunk.util.PlayerEntityExt
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object NetworkRegistry {
    val SEND_RESEARCH = Identifier(Magepunk.ID, "send_research")
    val DECREMENT_STACK = Identifier(Magepunk.ID, "decrement_stack")
    val UPDATE_ITEM = Identifier(Magepunk.ID, "update_item")
    val UPDATE_TECHS = Identifier(Magepunk.ID, "update_techs")

    fun registerServer() {
        Magepunk.log("Registering server networking")
        ServerPlayNetworking.registerGlobalReceiver(SEND_RESEARCH) { server, player, handler, buf, responseSender ->
            var item = buf.readItemStack()
            server.execute {
                val inv = player.inventory
                inv.insertStack(item)
                inv.markDirty()
            }
        }
        ServerPlayNetworking.registerGlobalReceiver(DECREMENT_STACK) { server, player, handler, buf, responseSender ->
            var item = buf.readItemStack()
            server.execute {
                val inv = player.inventory
                item.decrement(1)
                inv.markDirty()
            }
        }
        ServerPlayNetworking.registerGlobalReceiver(UPDATE_ITEM) { server, player, handler, buf, responseSender ->
            var item = buf.readItemStack()
            var slot = buf.readInt()
            val inv = player.inventory
            server.execute {
                if (slot != -1)
                    inv.setStack(slot, item)
                inv.markDirty()
            }
        }

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_TECHS) { server, player, handler, buf, responseSender ->
            NetworkingUtil.syncTechsS2C(player)
        }
    }

    fun registerClient() {
        Magepunk.LOGGER.info("Registring client networking")
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_TECHS) { client, handler, buf, responseSender ->
            var player = client.player
            var nbt = buf.readNbt()
            client.execute {
                if (nbt != null)
                    (player as IPlayerDataManager).setTechsNbt(nbt)
                (player as IClientPlayerExt).markClean()
            }
        }
    }
}