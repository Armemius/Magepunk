package com.armemius.magepunk.research.tech.render.components

import com.armemius.magepunk.Magepunk
import com.armemius.magepunk.gui.EnchiridionScreen
import com.armemius.magepunk.research.tech.render.IRenderComponent
import com.armemius.magepunk.util.RendererUtil
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

class WorkbenchCraftingRenderComponent(
    private val result: ItemStack,
    private val item1: ItemStack?, private val item2: ItemStack?, private val item3: ItemStack?,
    private val item4: ItemStack?, private val item5: ItemStack?, private val item6: ItemStack?,
    private val item7: ItemStack?, private val item8: ItemStack?, private val item9: ItemStack?
                                       ): IRenderComponent {
    private val GRID_TEXTURE = Identifier(Magepunk.ID, "textures/gui/research/basic_crafting_grid.png")
    override fun render(
        screenHandler: EnchiridionScreen,
        matrices: MatrixStack,
        x: Int,
        y: Int,
        mouseX: Int,
        mouseY: Int
    ) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.enableBlend()
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, GRID_TEXTURE)
        var wOffset = x + (190 - 112) / 2
        DrawableHelper.drawTexture(matrices, wOffset, y, 112, 70, 0f, 0f, 112, 70, 112, 70)
        if (item1 != null)
            screenHandler.getItemRenderer().renderGuiItemIcon(item1, wOffset + 4, y + 4)
        if (item2 != null)
            screenHandler.getItemRenderer().renderGuiItemIcon(item1, wOffset + 27, y + 4)
        if (item3 != null)
            screenHandler.getItemRenderer().renderGuiItemIcon(item1, wOffset + 50, y + 4)
        if (item4 != null)
            screenHandler.getItemRenderer().renderGuiItemIcon(item1, wOffset + 4, y + 27)
        if (item5 != null)
            screenHandler.getItemRenderer().renderGuiItemIcon(item1, wOffset + 27, y + 27)
        if (item6 != null)
            screenHandler.getItemRenderer().renderGuiItemIcon(item1, wOffset + 50, y + 27)
        if (item7 != null)
            screenHandler.getItemRenderer().renderGuiItemIcon(item1, wOffset + 4, y + 50)
        if (item8 != null)
            screenHandler.getItemRenderer().renderGuiItemIcon(item1, wOffset + 27, y + 50)
        if (item9 != null)
            screenHandler.getItemRenderer().renderGuiItemIcon(item1, wOffset + 50, y + 50)
        screenHandler.getItemRenderer().renderGuiItemIcon(result, wOffset + 92, y + 27)

        when (mouseY) {
            in (y + 4)..(y + 22) -> {
                when (mouseX) {
                    in (wOffset + 4)..(wOffset + 22) -> {
                        if (item1 != null)
                            screenHandler.renderTooltip(matrices, item1.name, mouseX, mouseY)
                    }
                    in (wOffset + 27)..(wOffset + 43) -> {
                        if (item2 != null)
                            screenHandler.renderTooltip(matrices, item2.name, mouseX, mouseY)
                    }
                    in (wOffset + 50)..(wOffset + 66) -> {
                        if (item3 != null)
                            screenHandler.renderTooltip(matrices, item3.name, mouseX, mouseY)
                    }
                }
            }
            in (y + 27)..(y + 43) -> {
                when (mouseX) {
                    in (wOffset + 4)..(wOffset + 22) -> {
                        if (item4 != null)
                            screenHandler.renderTooltip(matrices, item4.name, mouseX, mouseY)
                    }
                    in (wOffset + 27)..(wOffset + 43) -> {
                        if (item5 != null)
                            screenHandler.renderTooltip(matrices, item5.name, mouseX, mouseY)
                    }
                    in (wOffset + 50)..(wOffset + 66) -> {
                        if (item6 != null)
                            screenHandler.renderTooltip(matrices, item6.name, mouseX, mouseY)
                    }
                }
            }
            in (y + 50)..(y + 66) -> {
                when (mouseX) {
                    in (wOffset + 4)..(wOffset + 22) -> {
                        if (item7 != null)
                            screenHandler.renderTooltip(matrices, item7.name, mouseX, mouseY)
                    }
                    in (wOffset + 27)..(wOffset + 43) -> {
                        if (item8 != null)
                            screenHandler.renderTooltip(matrices, item8.name, mouseX, mouseY)
                    }
                    in (wOffset + 50)..(wOffset + 66) -> {
                        if (item9 != null)
                            screenHandler.renderTooltip(matrices, item9.name, mouseX, mouseY)
                    }
                }
            }
        }
        if (mouseX in (wOffset + 92)..(wOffset + 108)
            && (mouseY in (y + 27)..(y + 43))) {
            screenHandler.renderTooltip(matrices, result.name, mouseX, mouseY)
        }
    }

    override fun getHeight(): Int = 70
}