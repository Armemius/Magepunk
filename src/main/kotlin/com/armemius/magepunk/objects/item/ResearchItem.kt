package com.armemius.magepunk.objects.item

import com.armemius.magepunk.Magepunk
import com.armemius.magepunk.gui.ResearchScreen
import com.armemius.magepunk.research.tech.Tech
import com.armemius.magepunk.util.IPlayerDataManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.Rarity
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class ResearchItem: Item(Settings().group(Magepunk.ITEM_GROUP).maxCount(1).rarity(Rarity.UNCOMMON)) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val item = user.getStackInHand(hand)
        if (item.hasNbt()) {
            val nbt = item.nbt
            if (nbt != null) {
                if (nbt.contains("mgp.tech") && nbt.contains("mgp.stage")) {
                    var tech = Tech.TECHS_MAP[nbt.getString("mgp.tech")]!!
                    if (!tech.isAvailable(user)) {
                        if (world.isClient) {
                            user.sendMessage(Text.translatable("mgp.research.research_early").formatted(Formatting.LIGHT_PURPLE), true)
                        }
                    } else {
                        if (nbt.getBoolean("mgp.ready")) {
                            if (world.isClient) {
                                if (!tech.isFinished(user)) {
                                    user.sendMessage(Text.translatable("mgp.research.research_ready", Text.translatable(nbt.getString("mgp.tech"))).formatted(Formatting.AQUA), true)
                                }
                                else
                                    user.sendMessage(Text.translatable("mgp.research.research_already_finished").formatted(Formatting.RED), true)
                            }
                            if (!tech.isFinished(user)) {
                                tech.onFinished(user)
                                item.nbt = null
                                user.setStackInHand(hand, ItemStack.EMPTY)
                            }
                        }
                        else if (world.isClient)
                            MinecraftClient.getInstance().setScreen(ResearchScreen(user, item))
                    }
                }
            }
        }

        return super.use(world, user, hand)
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        if (stack.hasNbt()) {
            val nbt = stack.nbt
            if (nbt != null) {
                if (nbt.contains("mgp.tech") && nbt.contains("mgp.stage")) {
                    tooltip.add(Text.translatable("mgp.item.tooltip.tech").append(Text.of(": ")).formatted(Formatting.DARK_GRAY).append(Text.translatable(nbt.getString("mgp.tech")).formatted(Formatting.DARK_GREEN)))
                    if (nbt.getBoolean("mgp.ready")) {
                        tooltip.add(Text.translatable("mgp.item.tooltip.analyzable").formatted(Formatting.AQUA))
                    }
                }
            }
        }
        super.appendTooltip(stack, world, tooltip, context)
    }

    override fun allowNbtUpdateAnimation(
        player: PlayerEntity?,
        hand: Hand?,
        oldStack: ItemStack?,
        newStack: ItemStack?
    ): Boolean {
        return false
    }
}