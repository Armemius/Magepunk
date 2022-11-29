package com.armemius.magepunk.objects.item

import com.armemius.magepunk.Magepunk
import com.armemius.magepunk.gui.EnchiridionScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.Rarity
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class EnchiridionItem: Item(Settings().group(Magepunk.ITEM_GROUP).maxCount(1).rarity(Rarity.RARE)) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (world.isClient)
            MinecraftClient.getInstance().setScreen(EnchiridionScreen(user))

        return super.use(world, user, hand)
    }
}