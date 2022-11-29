package com.armemius.magepunk.research.tech.callbacks

import net.minecraft.entity.player.PlayerEntity

data class Callback(val player: PlayerEntity, val type: CallbackType, val packet: CallbackPackages.CallbackPackage)
