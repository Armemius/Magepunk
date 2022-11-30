package com.armemius.magepunk.research.tech.render.components

import com.armemius.magepunk.research.tech.render.IRenderComponent
import net.minecraft.text.Text

abstract class TextRenderComponent(protected val text: Text, protected val fontHeight: Int, protected val color: Int): IRenderComponent {}