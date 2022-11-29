package com.armemius.magepunk.objects.item

import com.armemius.magepunk.Magepunk
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Rarity
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import java.lang.Float.max
import kotlin.math.roundToInt

class InkwellWithFeatherItem: Item(Settings().group(Magepunk.ITEM_GROUP).rarity(Rarity.COMMON).maxCount(1)) {

    override fun getItemBarStep(stack: ItemStack): Int {
        if (stack.hasNbt() && stack.nbt != null && stack.nbt!!.contains("mgp.durability"))
            return (stack.nbt!!.getInt("mgp.durability") / 15f * 13.0f).roundToInt()
        return 13
    }
    override fun isItemBarVisible(stack: ItemStack): Boolean {
        return stack.hasNbt() && stack.nbt != null && stack.nbt!!.contains("mgp.durability") && stack.nbt!!.getInt("mgp.durability") < 15
    }

    override fun getItemBarColor(stack: ItemStack): Int {
        if (stack.hasNbt() && stack.nbt != null && stack.nbt!!.contains("mgp.durability")) {
            val f = max(0.0f, (stack.nbt!!.getInt("mgp.durability").toFloat() / 15f))
            return MathHelper.hsvToRgb(f / 3.0f, 1.0f, 1.0f)
        }
        return MathHelper.hsvToRgb(0.33f, 1.0f, 1.0f)
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext?
    ) {
        if (stack.hasNbt() && stack.nbt != null && stack.nbt!!.contains("mgp.durability") && stack.nbt!!.getInt("mgp.durability") == 0) {
            tooltip.add(Text.translatable("tooltip.mgp.inkwell_empty").formatted(Formatting.RED))
        }
    }
}