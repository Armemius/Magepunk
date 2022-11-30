package com.armemius.magepunk.research.tech.render

import com.armemius.magepunk.gui.EnchiridionScreen
import net.minecraft.client.util.math.MatrixStack

interface IRenderComponent {
    fun render(screenHandler: EnchiridionScreen, matrices: MatrixStack, x: Int, y: Int, mouseX: Int, mouseY: Int)
    fun getHeight(): Int
}
