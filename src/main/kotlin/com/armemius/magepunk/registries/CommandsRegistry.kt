package com.armemius.magepunk.registries

import com.armemius.magepunk.Magepunk
import com.armemius.magepunk.research.tech.Tech
import com.armemius.magepunk.util.IPlayerDataManager
import com.armemius.magepunk.util.NetworkingUtil
import com.armemius.magepunk.util.PlayerEntityExt
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object CommandsRegistry {
    fun register() {
        Magepunk.log("Commands registry")
        CommandRegistrationCallback.EVENT.register(CommandsRegistry::registerMgpCommand)
    }

    fun registerMgpCommand(dispatcher: CommandDispatcher<ServerCommandSource>, a: CommandRegistryAccess, r: CommandManager.RegistrationEnvironment) {
        dispatcher.register(
            CommandManager.literal("magepunk")
                .then(CommandManager.literal("network")
                    .then(CommandManager.literal("syncTechs")
                        .executes(::syncTechs)))
                .then(CommandManager.literal("researches")
                    .then(CommandManager.literal("reset")
                        .executes(::resetTechs))
                    .then(CommandManager.literal("gain")
                        .executes(::gainTechs)))
        )
    }

    private fun syncTechs(context: CommandContext<ServerCommandSource>): Int {
        var player = context.source.player
        player?.sendMessage(Text.of("Manually syncing server and client techs data"))
        if (player != null) {
            NetworkingUtil.syncTechsS2C(player)
        }
        return 1
    }

    private fun resetTechs(context: CommandContext<ServerCommandSource>): Int {
        var player = context.source.player
        if (player != null) {
            (player as IPlayerDataManager).setTechsNbt(NbtCompound())
            NetworkingUtil.syncTechsS2C(player)
        }
        return 1
    }

    private fun gainTechs(context: CommandContext<ServerCommandSource>): Int {
        var player = context.source.player
        if (player != null) {
            var nbt = (player as IPlayerDataManager).getTechsNbt()
            for (it in Tech.TECHS) {
                nbt.putBoolean("tech.mgp.data.${it.name}", true)
            }
            NetworkingUtil.syncTechsS2C(player)
        }
        return 1
    }
}