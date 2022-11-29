package com.armemius.magepunk.util

import com.mojang.blaze3d.platform.GlStateManager.DstFactor
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.texture.TextureManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import java.lang.reflect.Field

object RendererUtil {
    fun ItemRenderer.renderItemIcon(stack: ItemStack, x: Int, y: Int, koef: Float) {
        try {
            var textureMangerField: Field = ItemRenderer::class.java.getDeclaredField("textureManager")
            textureMangerField.isAccessible = true
            var textureManager = (textureMangerField.get(this) as TextureManager)
            var model = this.getModel(stack, null as World?, null as LivingEntity?, 0)
            textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false)
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
            RenderSystem.enableBlend()
            RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA)
            val matrixStack = RenderSystem.getModelViewStack()
            matrixStack.push()
            matrixStack.translate(x.toDouble(), y.toDouble(), (100.0f + this.zOffset).toDouble())
            matrixStack.translate(8.0, 8.0, 0.0)
            matrixStack.scale(1.0f, -1.0f, 1.0f)
            matrixStack.scale(16.0f, 16.0f, 16.0f)
            RenderSystem.applyModelViewMatrix()
            val matrixStack2 = MatrixStack()
            val immediate = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
            val bl: Boolean = !model.isSideLit()
            if (bl) {
                DiffuseLighting.disableGuiDepthLighting()
            }

            this.renderItem(
                stack,
                ModelTransformation.Mode.GUI,
                false,
                matrixStack2,
                immediate,
                (255 * koef).toInt(),
                OverlayTexture.DEFAULT_UV,
                model
            )
            immediate.draw()
            RenderSystem.enableDepthTest()
            if (bl) {
                DiffuseLighting.enableGuiDepthLighting()
            }

            matrixStack.pop()
            RenderSystem.applyModelViewMatrix()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Draw text
    fun renderText(matrices: MatrixStack, textRenderer: TextRenderer, text: Text, x: Int, y: Int, fontHeight: Int, color: Int) {
        val mult = fontHeight.toFloat() / 9f
        matrices.push()
        matrices.scale(mult, mult, mult)
        textRenderer.draw(matrices, text, x.toFloat() / mult, y.toFloat() / mult, color)
        matrices.pop()
    }

    // Draw centered text without shadow
    fun renderCenteredText(matrices: MatrixStack, textRenderer: TextRenderer, text: Text, x: Int, y: Int, fontHeight: Int, color: Int) {
        val mult = fontHeight.toFloat() / 9f
        matrices.push()
        matrices.scale(mult, mult, mult)
        textRenderer.draw(matrices, text, (x - textRenderer.getWidth(text) / 2).toFloat() / mult, (y).toFloat() / mult, color)
        matrices.pop()
    }

    // Returns height of rendered text
    fun renderPlainText(matrices: MatrixStack, textRenderer: TextRenderer, text: Text, x: Int, y: Int, width: Int, fontHeight: Int, color: Int): Int {
        val mult = fontHeight.toFloat() / 9f
        matrices.push()
        matrices.scale(mult, mult, mult)
        val lineHeight = (9 + 5) * mult
        val words = text.string.split("\\s".toRegex())
        var buff = ""
        var lineCount = 0
        for (str in words) {
            if (textRenderer.getWidth("$buff $str") * mult >= width) {
                textRenderer.draw(matrices, buff, x.toFloat() / mult, y.toFloat() / mult + lineHeight * lineCount, color)
                lineCount++
                buff = str
            } else {
                buff += if (buff == "") "$str" else " $str"
            }
        }
        textRenderer.draw(matrices, buff, x.toFloat() / mult, y.toFloat() / mult + lineHeight * lineCount, -16777216)
        matrices.pop()
        return (lineHeight * (1 + lineCount)).toInt() + 1
    }
}