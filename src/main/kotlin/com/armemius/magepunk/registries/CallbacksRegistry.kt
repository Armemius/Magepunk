package com.armemius.magepunk.registries

import com.armemius.magepunk.Magepunk
import com.armemius.magepunk.research.tech.callbacks.Callback
import com.armemius.magepunk.research.tech.callbacks.CallbackPackages
import com.armemius.magepunk.research.tech.callbacks.CallbackType
import com.armemius.magepunk.util.NetworkingUtil
import com.armemius.magepunk.util.PlayerEntityExt
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

object CallbacksRegistry {
    fun register() {
        Magepunk.log("Callbacks registry")
        AttackBlockCallback.EVENT.register(AttackBlockCallback { player: PlayerEntity, world: World, hand: Hand, pos: BlockPos, direction: Direction ->
            val state = world.getBlockState(pos)
            if (!player.isSpectator && player is ServerPlayerEntity)
                (player as PlayerEntityExt).sendCallback(Callback(player, CallbackType.ATTACK_BLOCK, CallbackPackages.AttackBlock(world, pos, state)))
            ActionResult.PASS
        })
        UseItemCallback.EVENT.register(UseItemCallback { player, world, hand ->
            if (player is ServerPlayerEntity)
                (player as PlayerEntityExt).sendCallback(Callback(player, CallbackType.USE_ITEM, CallbackPackages.UseItem(world, player.getStackInHand(hand))))
            TypedActionResult.pass(ItemStack.EMPTY);
        })
        AttackEntityCallback.EVENT.register(AttackEntityCallback { player, world, hand, entity, hitResult ->
            if (player is ServerPlayerEntity)
                (player as PlayerEntityExt).sendCallback(Callback(player, CallbackType.ATTACK_ENTITY, CallbackPackages.AttackEntity(world, hand, entity, hitResult)))
            ActionResult.PASS
        })
        UseBlockCallback.EVENT.register(UseBlockCallback { player, world, hand, hitResult ->
            if (player is ServerPlayerEntity)
                (player as PlayerEntityExt).sendCallback(Callback(player, CallbackType.USE_BLOCK, CallbackPackages.UseBlock(world, hand, hitResult)))
            ActionResult.PASS
        })
    }
}