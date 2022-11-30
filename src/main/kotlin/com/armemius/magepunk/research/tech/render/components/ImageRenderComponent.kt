package com.armemius.magepunk.research.tech.render.components

import com.armemius.magepunk.gui.EnchiridionScreen
import com.armemius.magepunk.research.tech.render.IRenderComponent
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class ImageRenderComponent(private val img: Identifier, private val w: Int, private val h: Int): IRenderComponent {
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
        RenderSystem.setShaderTexture(0, img)
        DrawableHelper.drawTexture(matrices, x + (190 - w) / 2, y, w, h, 0f, 0f, w, h, w, h)
    }

    override fun getHeight(): Int = h
}