package com.armemius.magepunk.research.tech.render.components

import com.armemius.magepunk.gui.EnchiridionScreen
import com.armemius.magepunk.research.tech.render.IRenderComponent
import com.armemius.magepunk.util.RendererUtil
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class TitleRenderComponent(text: Text, fontHeight: Int, color: Int): TextRenderComponent(text, fontHeight, color) {
    override fun render(
        screenHandler: EnchiridionScreen,
        matrices: MatrixStack,
        x: Int,
        y: Int,
        mouseX: Int,
        mouseY: Int
    ) {
        RendererUtil.renderCenteredText(matrices, screenHandler.getTextRenderer(), text, x + 95, y, fontHeight, color)
    }

    override fun getHeight(): Int = fontHeight + 1
}