package com.armemius.magepunk.research.tech.render.components

import com.armemius.magepunk.gui.EnchiridionScreen
import com.armemius.magepunk.research.tech.render.IRenderComponent
import net.minecraft.client.util.math.MatrixStack

class EmptyRenderComponent(private val h: Int = 0): IRenderComponent {
    override fun render(
        screenHandler: EnchiridionScreen,
        matrices: MatrixStack,
        x: Int,
        y: Int,
        mouseX: Int,
        mouseY: Int
    ) {}

    override fun getHeight(): Int = h
}