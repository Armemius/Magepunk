package com.armemius.magepunk.research.tech.render.components

import com.armemius.magepunk.gui.EnchiridionScreen
import com.armemius.magepunk.research.tech.render.IRenderComponent
import com.armemius.magepunk.util.RendererUtil
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class PlainTextRendererComponent(text: Text, fontHeight: Int, color: Int): TextRenderComponent(text, fontHeight, color) {
    private var height: Int = 0

    override fun render(
        screenHandler: EnchiridionScreen,
        matrices: MatrixStack,
        x: Int,
        y: Int,
        mouseX: Int,
        mouseY: Int
    ) {
        height = RendererUtil.renderPlainText(matrices, screenHandler.getTextRenderer(), text, x + 20, y, 160, fontHeight, color)
    }

    override fun getHeight(): Int = height
}