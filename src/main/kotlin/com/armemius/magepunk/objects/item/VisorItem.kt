package com.armemius.magepunk.objects.item

import com.armemius.magepunk.Magepunk
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class VisorItem : Item(
    Settings()
        .group(Magepunk.ITEM_GROUP)
        .maxCount(1)
) {

    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        return TypedActionResult.pass(user?.getStackInHand(hand))
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if (!context.world.isClient)
            context.player?.sendMessage(Text.translatable(context.world.getBlockState(context.blockPos).block.translationKey), true)
        return ActionResult.SUCCESS
    }

    override fun canMine(state: BlockState?, world: World?, pos: BlockPos?, miner: PlayerEntity?): Boolean {
        return false
    }
}