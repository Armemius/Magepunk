package com.armemius.magepunk.gui

import com.armemius.magepunk.Magepunk
import com.armemius.magepunk.registries.NetworkRegistry
import com.armemius.magepunk.research.tech.Tech
import com.armemius.magepunk.research.tech.TechStage
import com.armemius.magepunk.util.RendererUtil
import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.NarratorManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class ResearchScreen(val player: PlayerEntity, val item: ItemStack) : Screen(NarratorManager.EMPTY) {
    private val SCREEN_TEXTURE = Identifier(Magepunk.ID, "textures/gui/research/research.png")
    private val BUTTONS_TEXTURE = Identifier(Magepunk.ID, "textures/gui/research/buttons.png")
    private var name: Text? = null
    private var stageNumber = 0
    private var tech: Tech? = null
    private var stage: TechStage? = null

    override fun init() {
        var raw = item.nbt!!.getString("mgp.tech")
        stageNumber = item.nbt!!.getInt("mgp.stage")
        name = Text.translatable(raw)
        tech = Tech.TECHS_MAP[raw]!!
        stage = tech!!.stages[stageNumber]
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val wOffset = (width - 192) / 2
        val hOffset = (height - 256) / 2
        this.renderBackground(matrices)
        this.drawScreen(matrices, wOffset, hOffset)
        this.drawTooltips(matrices, wOffset, hOffset, mouseX, mouseY)
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val slot = player.inventory.getSlotWithStack(item)
        if (stage != null && stage!!.checkIfReady(item)) {
            var shouldClose = false
            if (stageNumber + 1 == tech!!.stages.size) {
                item.nbt!!.putBoolean("mgp.ready", true)
                shouldClose = true
            } else {
                item.nbt!!.putInt("mgp.stage", stageNumber + 1)
                tech!!.stages[stageNumber + 1].reqs.forEachIndexed { index, _ ->
                    item.nbt!!.putFloat("mgp.req.slot$index", 0f)
                }
                init()
            }
            val buf = PacketByteBufs.create()
            buf.writeItemStack(item)
            buf.writeInt(slot)
            ClientPlayNetworking.send(NetworkRegistry.UPDATE_ITEM, buf)
            if (shouldClose)
                this.close()
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun drawScreen(matrices: MatrixStack, x: Int, y: Int) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.enableBlend()
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, SCREEN_TEXTURE)
        DrawableHelper.drawTexture(matrices, x, y, 0f, 0f, 192, 256, 192, 256)
        if (name != null)
            RendererUtil.renderCenteredText(matrices, textRenderer, name!!, x + 96, y + 15, 12, 0xFF171717.toInt())
        if (stage != null) {
            stage!!.reqs.forEachIndexed { index, it ->
                var progress = item.nbt!!.getFloat("mgp.req.slot$index")
                val color = when(progress) {
                    0f -> 0xFFAB250A.toInt()
                    1f -> 0xFF0AAB20.toInt()
                    else -> 0xFFB0AB1A.toInt()
                }
                val status = when(progress) {
                    0f -> Text.translatable("mgp.research.not_ready")
                    1f -> Text.translatable("mgp.research.ready")
                    else -> Text.translatable("mgp.research.in_progress", (progress * 100).toInt())
                }
                RendererUtil.renderText(matrices, textRenderer, Text.translatable(it.id).append(Text.of(" . . ")).append(status), x + 10, y + 36 + index * 10, 7, color)
            }
        }
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableBlend()

        RenderSystem.setShaderTexture(0, BUTTONS_TEXTURE)
        if (stage != null && !stage!!.checkIfReady(item))
            RenderSystem.setShaderColor(0.15f, 0.15f, 0.15f, 0.9f)
        this.drawTexture(matrices, x + 142, y + 206, 0, 0, 38, 38)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    }

    private fun drawTooltips(matrices: MatrixStack, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        if (mouseX in (x + 142)..(x + 180) && mouseY in (y + 206)..(y + 244)) {
            this.renderTooltip(matrices, getTooltip(), mouseX, mouseY)
        }
    }

    private fun getTooltip(): Text {
        if (stage != null && stage!!.checkIfReady(item)) {
            if (stageNumber + 1 == tech!!.stages.size)
                return Text.translatable("mgp.research.analyze").formatted(Formatting.GREEN)
            return Text.translatable("mgp.research.continue").formatted(Formatting.GREEN)
        }
        return Text.translatable("mgp.research.techs_not_ready").formatted(Formatting.RED)
    }
}