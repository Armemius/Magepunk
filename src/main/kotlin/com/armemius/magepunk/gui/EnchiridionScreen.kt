package com.armemius.magepunk.gui

import com.armemius.magepunk.Magepunk
import com.armemius.magepunk.registries.ItemsRegistry
import com.armemius.magepunk.registries.NetworkRegistry
import com.armemius.magepunk.research.ResearchHandler
import com.armemius.magepunk.research.tab.Tab
import com.armemius.magepunk.research.tech.Difficulty
import com.armemius.magepunk.research.tech.Shape
import com.armemius.magepunk.research.tech.Tech
import com.armemius.magepunk.util.RendererUtil
import com.armemius.magepunk.util.RendererUtil.renderItemIcon
import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.NarratorManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import kotlin.math.sin

class EnchiridionScreen(private var player: PlayerEntity) : Screen(NarratorManager.EMPTY) {
    private val SCREEN_TEXTURE = Identifier(Magepunk.ID, "textures/gui/research/research_screen.png")
    private val TECHS_INTERACTION_SCREEN_TEXTURE = Identifier(Magepunk.ID, "textures/gui/research/info.png")
    private val ARROWS_TEXTURE = Identifier(Magepunk.ID, "textures/gui/research/arrows.png")
    private val BUTTONS_TEXTURE = Identifier(Magepunk.ID, "textures/gui/research/buttons.png")
    private val TABS_TEXTURE = Identifier(Magepunk.ID, "textures/gui/research/tabs.png")

    var wOffset = (width - 256) / 2
    var hOffset = (height - 256) / 2
    var tabs: List<Tab> = listOf()
    var openedTechs: MutableList<Tech> = mutableListOf()
    var lockedTechs: MutableList<Tech> = mutableListOf()
    var availableTechs: MutableList<Tech> = mutableListOf()
    var activeTab: Int = 0
    var shiftX: Double = 0.0
    var shiftY: Double = 0.0
    var frame: Int = 0
    var activeConfirmScreen: Tech? = null
    var activeInfoScreen: Tech? = null
    var infoPage: Int = 0

    var mouseOverLeftArrow = false
    var mouseOverRightArrow = false

    override fun init() {
        tabs = ResearchHandler.getAvailableTabs(player)
        if (tabs.isEmpty())
            this.close()
        updateTechs()
        super.init()
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        wOffset = (width - 256) / 2
        hOffset = (height - 256) / 2
        if (!blockMainScreen()) {
            renderBackground(matrices)
        }
        drawContent(matrices, wOffset, hOffset)
        drawScreen(matrices, wOffset, hOffset)
        if (blockMainScreen()) {
            renderBackground(matrices)
        }
        drawConfirmScreen(matrices, mouseX, mouseY)
        drawInfoScreen(matrices, mouseX, mouseY)
        drawTooltips(matrices, mouseX, mouseY)
        super.render(matrices, mouseX, mouseY, delta)
    }

    // Util
    private fun rgb(r: Int, g: Int, b: Int, a: Int): Int =
        ((a shl 24) or (r shl 16) or (g shl 8) or b)

    private fun blockMainScreen() = (activeInfoScreen != null || activeConfirmScreen != null)

    private fun getActiveTab(): Tab = tabs[activeTab]

    private fun updateTechs() {
        openedTechs = mutableListOf()
        lockedTechs = mutableListOf()
        availableTechs = mutableListOf()
        for (tech in getActiveTab().techs) {
            if (!tech.isVisible(player))
                continue
            if (tech.isFinished(player))
                openedTechs.add(tech)
            else if (tech.isAvailable(player))
                availableTechs.add(tech)
            else
                lockedTechs.add(tech)
        }
    }

    private fun resetTab(index: Int) {
        shiftX = 0.0
        shiftY = 0.0
        activeTab = index
        updateTechs()
    }

    private fun mouseOnTech(tech: Tech, x: Int, y: Int): Boolean =
        x.toDouble() in (wOffset + 120 + shiftX + tech.posX)..(wOffset + 140 + shiftX + tech.posX)
            && y.toDouble() in (hOffset + 120 + shiftY + tech.posY)..(hOffset + 140 + shiftY + tech.posY)

    private fun ableToResearch(): Boolean {
        val inv = player.inventory
        for (it in 0..(inv.size())) {
            val item = inv.getStack(it)
            if (item.nbt?.getString("mgp.tech") == activeConfirmScreen?.name && item.item == ItemsRegistry.RESEARCH)
                return false
        }
        if (inv.emptySlot != -1
            && ((inv.contains(ItemStack(Items.PAPER))
                    && getInkwellSlot() >= 0
                    )
                    || player.isCreative)
        )
            return true
        return false
    }

    private fun getInkwellSlot(): Int {
        val inv = player.inventory
        var contains = false
        for (it in 0 until inv.size()) {
            if (inv.getStack(it).item == ItemsRegistry.INKWELL_WITH_FEATHER) {
                contains = true
                val item = inv.getStack(it)
                if (!item.hasNbt()
                    || item.nbt == null
                    || item.nbt!!.getInt("mgp.durability") > 0)
                    return it
            }
        }
        if (contains)
            return -2
        return -1
    }

    private fun researchStartStat(): Text {
        val inv = player.inventory
        for (it in 0..(inv.size())) {
            val item = inv.getStack(it)
            if (item.nbt?.getString("mgp.tech") == activeConfirmScreen?.name && item.item == ItemsRegistry.RESEARCH)
                return Text.translatable("mgp.research.have_notes").formatted(Formatting.RED)
        }
        if (inv.emptySlot == -1)
            return Text.translatable("mgp.research.no_slots").formatted(Formatting.RED)
        if (player.isCreative)
            return Text.translatable("mgp.research.start").formatted(Formatting.GREEN)
        if (!inv.contains(ItemStack(Items.PAPER)))
            return Text.translatable("mgp.research.no_paper").formatted(Formatting.RED)
        var stat = getInkwellSlot()
        if (stat == -1)
            return Text.translatable("mgp.research.no_pen").formatted(Formatting.RED)
        if (stat == -2)
            return Text.translatable("mgp.research.inkwell_empty").formatted(Formatting.RED)

        return Text.translatable("mgp.research.start").formatted(Formatting.GREEN)
    }

    fun getTextRenderer(): TextRenderer {
        return textRenderer
    }

    // Rendering part
    private fun drawContent(matrices: MatrixStack, x: Int, y: Int) {
        val matrixStack = RenderSystem.getModelViewStack()
        matrixStack.push()
        matrixStack.translate((x + 16).toDouble(), (y + 16).toDouble(), 0.0)
        RenderSystem.applyModelViewMatrix()
        matrices.push()
        matrices.translate(0.0, 0.0, 950.0)
        RenderSystem.enableDepthTest()
        RenderSystem.colorMask(false, false, false, false)
        fill(matrices, 4680, 2260, -4680, -2260, -16777216)
        RenderSystem.colorMask(true, true, true, true)
        matrices.translate(0.0, 0.0, -950.0)
        RenderSystem.depthFunc(518)
        fill(matrices, 224, 224, 0, 0, -16777216)
        RenderSystem.depthFunc(515)
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, getActiveTab().background)

        val k = shiftX.toInt() % 1024
        val l = shiftY.toInt() % 1024
        DrawableHelper.drawTexture(matrices, -140 + (k * 0.5).toInt(), -140 + (l * 0.5).toInt(), 512, 512, 0f, 0f, 1024, 1024, 1024, 1024)
        //drawTexture(matrices, -384 + (k * 0.7).toInt(), -384 + (l * 0.7).toInt(), 0.0f, 0.0f, 1024, 1024, 1024, 1024)

        val renderLines = { tech: Tech, dest: Tech, color: Int ->
            if (dest.posY == tech.posY) {
                this.drawHorizontalLine(matrices, 114 + shiftX.toInt() + tech.posX, 114 + shiftX.toInt() + dest.posX, 114 + shiftY.toInt() + dest.posY,
                    color
                )
            } else if (dest.posX == tech.posX) {
                this.drawVerticalLine(matrices, 114 + shiftX.toInt() + tech.posX, 114 + shiftY.toInt() + tech.posY, 114 + shiftY.toInt() + dest.posY,
                    color
                )
            } else {
                this.drawHorizontalLine(matrices, 114 + shiftX.toInt() + tech.posX, 114 + shiftX.toInt() + dest.posX, 114 + shiftY.toInt() + dest.posY,
                    color
                )
                this.drawVerticalLine(matrices, 114 + shiftX.toInt() + tech.posX, 114 + shiftY.toInt() + tech.posY, 114 + shiftY.toInt() + dest.posY,
                    color
                )
            }
        }

        val anim = sin(Math.PI * (frame % 40) / 40).toFloat()
        availableTechs.forEach {
            it.links.forEach { jt ->
                val color = rgb(0, 0, 0, 125 + (100.0 * anim).toInt())
                renderLines(it, jt, color)
            }
        }
        openedTechs.forEach {
            val color = rgb(0, 0, 0, 225)
            it.links.forEach { jt ->
                renderLines(it, jt, color)
            }
        }
        lockedTechs.forEach {
            it.links.forEach { jt ->
                val color = rgb(0, 0, 0, 175)
                renderLines(it, jt, color)
            }
        }

        val drawTech = { tech: Tech, koef: Float ->
            RenderSystem.setShaderTexture(0, TABS_TEXTURE)
            this.drawTexture(
                matrices,
                104 + tech.posX + shiftX.toInt(),
                104 + tech.posY + shiftY.toInt(),
                when(tech.shape) {
                    Shape.SQUARE -> 0
                    Shape.ROUND -> 20
                    Shape.HEXAGON -> 40
                    Shape.STAR -> 60
                },
                28,
                20,
                20
            )
            itemRenderer.renderItemIcon(tech.icon.defaultStack, 106 + tech.posX + shiftX.toInt(), 106 + tech.posY + shiftY.toInt(), koef)
        }

        for (tech in openedTechs) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            drawTech(tech, 1.0f)
        }

        for (tech in availableTechs) {
            val koef = 0.3f + anim * 0.7f
            RenderSystem.setShaderColor(koef, koef, koef, 1.0f)
            drawTech(tech, koef)
        }

        for (tech in lockedTechs) {
            RenderSystem.setShaderColor(0.2f, 0.2f, 0.2f, 1.0f)
            drawTech(tech, 0f)
        }

        RenderSystem.depthFunc(518)
        matrices.translate(0.0, 0.0, -950.0)
        RenderSystem.colorMask(false, false, false, false)
        fill(matrices, 4680, 2260, -4680, -2260, -16777216)
        RenderSystem.colorMask(true, true, true, true)
        RenderSystem.depthFunc(515)
        matrices.pop()
        matrixStack.pop()
        RenderSystem.applyModelViewMatrix()
        RenderSystem.depthFunc(515)
        RenderSystem.disableDepthTest()
    }

    private fun drawScreen(matrices: MatrixStack, x: Int, y: Int) {
        RenderSystem.enableDepthTest()
        tabs.forEachIndexed { index, it ->
            RenderSystem.setShaderTexture(0, TABS_TEXTURE)
            if (index == activeTab) {
                this.drawTexture(matrices, x - 27, y + 30 * index + 2, 21, 0, 27, 28)
                itemRenderer.renderItemIcon(it.icon.defaultStack, x - 27 + (27 - 16) / 2, y + 30 * index + 2 + (28 - 16) / 2, 1f)
            } else {
                this.drawTexture(matrices, x - 21, y + 30 * index + 2, 0, 0, 21, 28)
                itemRenderer.renderItemIcon(it.icon.defaultStack, x - 21 + (27 - 16) / 2, y + 30 * index + 2 + (28 - 16) / 2, 0.75f)
            }
        }
        RenderSystem.depthFunc(518)
        RenderSystem.applyModelViewMatrix()
        RenderSystem.depthFunc(515)
        RenderSystem.disableDepthTest()

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.enableBlend()
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, SCREEN_TEXTURE)
        this.drawTexture(matrices, x, y, 0, 0, 256, 256)

        RenderSystem.defaultBlendFunc()
        RenderSystem.disableBlend()
    }

    private fun drawTooltips(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        if (!blockMainScreen()) {
            matrices.push()
            tabs.forEachIndexed {index, tab ->
                if (index == activeTab) {
                    if (mouseX in (wOffset - 27)..wOffset
                        && mouseY in (hOffset + 30 * index + 2)..(hOffset + 30 * index + 30)) {
                        this.renderTooltip(matrices, Text.translatable(tab.name), mouseX, mouseY)
                    }
                } else {
                    if (mouseX in (wOffset - 21)..wOffset
                        && mouseY in (hOffset + 30 * index + 2)..(hOffset + 30 * index + 30)) {
                        this.renderTooltip(matrices, Text.translatable(tab.name), mouseX, mouseY)
                    }
                }
            }
            if (mouseX in (wOffset + 16)..(wOffset + 240)
                && mouseY in (hOffset + 16)..(hOffset + 240)
            ) {
                openedTechs.forEach {
                    if (mouseOnTech(it, mouseX, mouseY)) {
                        this.renderTooltip(matrices, Text.translatable(it.name), mouseX, mouseY)
                    }
                }
                lockedTechs.forEach {
                    if (mouseOnTech(it, mouseX, mouseY)) {
                        this.renderTooltip(matrices, Text.translatable("tech.mgp.locked"), mouseX, mouseY)
                    }
                }
                availableTechs.forEach {
                    if (mouseOnTech(it, mouseX, mouseY)) {
                        this.renderTooltip(matrices, Text.translatable(it.name), mouseX, mouseY)
                    }
                }
            }
            matrices.pop()
        }
        var wInfoOffset = (width - 200) / 2
        var hInfoOffset = (height - 256) / 2
        if (activeConfirmScreen != null) {
            if (mouseX in (wInfoOffset + 141)..(wInfoOffset + 179)
                && mouseY in (hInfoOffset + 209)..(hInfoOffset + 247)) {
                this.renderTooltip(matrices, researchStartStat(), mouseX, mouseY)
            }
        }
    }

    private fun drawConfirmScreen(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        if (activeConfirmScreen == null)
            return
        var wInfoOffset = (width - 200) / 2
        var hInfoOffset = (height - 256) / 2
        this.renderBackground(matrices)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.enableBlend()
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, TECHS_INTERACTION_SCREEN_TEXTURE)
        DrawableHelper.drawTexture(matrices, wInfoOffset, hInfoOffset, 200, 256, 0f, 0f, 200, 256, 200, 256)
        var color: Formatting? = when(activeConfirmScreen!!.diff) {
            Difficulty.EASY -> Formatting.GREEN
            Difficulty.MEDIUM -> Formatting.YELLOW
            Difficulty.HARD -> Formatting.RED
            Difficulty.INSANE -> Formatting.LIGHT_PURPLE
            Difficulty.IMPOSSIBLE -> Formatting.DARK_PURPLE
            else -> Formatting.DARK_GRAY
        }
        var diffText: String = when(activeConfirmScreen!!.diff) {
            Difficulty.EASY -> "mgp.tech.diff.easy"
            Difficulty.MEDIUM -> "mgp.tech.diff.medium"
            Difficulty.HARD -> "mgp.tech.diff.hard"
            Difficulty.INSANE -> "mgp.tech.diff.insane"
            Difficulty.IMPOSSIBLE -> "mgp.tech.diff.impossible"
            Difficulty.NONE -> "mgp.tech.diff.none"
        }

        matrices.push()
        matrices.scale(1.4f, 1.4f, 1.4f)
        var text = Text.translatable(activeConfirmScreen!!.name).asOrderedText()
        this.textRenderer.draw(matrices, text, ((wInfoOffset + 92 - textRenderer.getWidth(text).toFloat() / 2)/ 1.4).toFloat() , ((hInfoOffset + 10) / 1.4).toFloat(), -16777216)
        matrices.pop()

        RendererUtil.renderPlainText(matrices, textRenderer, Text.translatable("${activeConfirmScreen!!.name}.desc"), wInfoOffset + 10, hInfoOffset + 30, 172, 8, 0xFF0F0F12.toInt())

        matrices.push()
        matrices.scale(0.7f, 0.7f, 0.7f)
        this.textRenderer.draw(matrices, Text.translatable("mgp.tech.diff").append(Text.of(": ")).append(Text.translatable(diffText).formatted(color)), (wInfoOffset.toFloat() + 7f) / 0.7f, (hInfoOffset.toFloat() + 240) / 0.7f, -16777216)
        matrices.pop()

        RenderSystem.setShaderTexture(0, BUTTONS_TEXTURE)
        if (!ableToResearch())
            RenderSystem.setShaderColor(0.3f, 0.25f, 0.25f, 0.9f)
        this.drawTexture(matrices, wInfoOffset + 141, hInfoOffset + 209, 0, 0, 38, 38)

        RenderSystem.defaultBlendFunc()
        RenderSystem.disableBlend()
    }

    private fun drawInfoScreen(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        if (activeInfoScreen == null)
            return
        var wInfoOffset = (width - 200) / 2
        var hInfoOffset = (height - 256) / 2
        this.renderBackground(matrices)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.enableBlend()
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, TECHS_INTERACTION_SCREEN_TEXTURE)
        DrawableHelper.drawTexture(matrices, wInfoOffset, hInfoOffset, 200, 256, 0f, 0f, 200, 256, 200, 256)
        var height = 10
        for (it in activeInfoScreen!!.infoPages[infoPage].renderComponents) {
            it.render(this, matrices, wInfoOffset, hInfoOffset + height, mouseX, mouseY)
            height += it.getHeight() + 2
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.disableBlend()
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, ARROWS_TEXTURE)

        if (infoPage > 0) {
            DrawableHelper.drawTexture(matrices, wInfoOffset + 10, hInfoOffset + 240, 0f, 0f, 16, 8, 16, 8)
//            this.drawTexture(
//                matrices,
//                wInfoOffset + 10,
//                hInfoOffset + 240,
//                0,
//                0,
//                16,
//                8
//            )
        }
        // TODO
        if (infoPage < activeInfoScreen!!.infoPages.size - 1) {
            DrawableHelper.drawTexture(matrices, wInfoOffset + 164, hInfoOffset + 240, 0f, 8f, 16, 8, 16, 8)
//            this.drawTexture(
//                matrices,
//                wInfoOffset + 164,
//                hInfoOffset + 240,
//                0,
//                0,
//                16,
//                8
//            )
        }
    }

    // Functional part
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!blockMainScreen()) {
            tabs.forEachIndexed { index, _ ->
                if (index == activeTab) {
                    if (mouseX.toInt() in (wOffset - 27)..wOffset
                        && mouseY.toInt() in (hOffset + 30 * index + 2)..(hOffset + 30 * index + 30)) {
                        player.playSound(SoundEvents.AMBIENT_CAVE, 0.6f, 1.0f)
                        resetTab(index)
                    }
                } else {
                    if (mouseX.toInt() in (wOffset - 21)..wOffset
                        && mouseY.toInt() in (hOffset + 30 * index + 2)..(hOffset + 30 * index + 30)) {
                        player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.6f, 1.0f)
                        resetTab(index)
                    }
                }
            }

            openedTechs.forEach {
                if (mouseOnTech(it, mouseX.toInt(), mouseY.toInt())) {
                    infoPage = 0
                    activeInfoScreen = it
                }
            }
            availableTechs.forEach {
                if (mouseOnTech(it, mouseX.toInt(), mouseY.toInt())) {
                    activeConfirmScreen = it
                }
            }
        }

        if (activeConfirmScreen != null) {
            var wInfoOffset = (width - 200) / 2
            var hInfoOffset = (height - 256) / 2
            if (mouseX.toInt() in (wInfoOffset + 141)..(wInfoOffset + 179)
                && mouseY.toInt() in (hInfoOffset + 209)..(hInfoOffset + 247)) {
                if (ableToResearch()) {
                    MinecraftClient.getInstance().player?.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1f, 1f)
                    val inv = player.inventory
                    var item = ItemStack(ItemsRegistry.RESEARCH, 1)
                    var nbt = NbtCompound()
                    nbt.putString("mgp.tech", activeConfirmScreen!!.name)
                    nbt.putInt("mgp.stage", 0)
                    nbt.putBoolean("mgp.ready", false)
                    activeConfirmScreen!!.stages[0].reqs.forEachIndexed { index, _ ->
                        nbt.putFloat("mgp.req.slot${index}", 0f)
                    }
                    item.nbt = nbt
                    if (!player.isCreative) {
                        val paperItem = inv.getSlotWithStack(ItemStack(Items.PAPER))
                        val inkwellSlot = getInkwellSlot()
                        val inkwellItem = inv.getStack(inkwellSlot)
                        if (!inkwellItem.hasNbt() || inkwellItem.nbt == null) {
                            val nbt = NbtCompound()
                            nbt.putInt("mgp.durability", 15)
                            inkwellItem.nbt = nbt
                        }
                        inkwellItem.nbt!!.putInt("mgp.durability", inkwellItem.nbt!!.getInt("mgp.durability") - 1)
                        val buf = PacketByteBufs.create()
                        buf.writeItemStack(item)
                        ClientPlayNetworking.send(NetworkRegistry.SEND_RESEARCH, buf)
                        val buf2 = PacketByteBufs.create()
                        buf2.writeItemStack(inv.getStack(paperItem))
                        ClientPlayNetworking.send(NetworkRegistry.DECREMENT_STACK, buf2)
                        val buf3 = PacketByteBufs.create()
                        buf3.writeItemStack(inkwellItem)
                        buf3.writeInt(inkwellSlot)
                        ClientPlayNetworking.send(NetworkRegistry.UPDATE_ITEM, buf3)

                        inv.getStack(paperItem).decrement(1)
                        player.giveItemStack(item)
                    } else {
                        val buf = PacketByteBufs.create()
                        buf.writeItemStack(item)
                        ClientPlayNetworking.send(NetworkRegistry.SEND_RESEARCH, buf)
                        player.giveItemStack(item)
                    }

                    this.activeConfirmScreen = null
                }
            }
        }
        if (activeInfoScreen != null) {
            var wInfoOffset = (width - 200) / 2
            var hInfoOffset = (height - 256) / 2
            if (infoPage > 0
                && mouseX.toInt() in (wInfoOffset + 10)..(wInfoOffset + 26)
                && mouseY.toInt() in (hInfoOffset + 240)..(hInfoOffset + 248)) {
                infoPage--
            }
            // TODO
            if (infoPage < activeInfoScreen!!.infoPages.size - 1
                && mouseX.toInt() in (wInfoOffset + 164)..(wInfoOffset + 180)
                && mouseY.toInt() in (hInfoOffset + 240)..(hInfoOffset + 248)) {
                infoPage++
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (!blockMainScreen()) {
            if (mouseX.toInt() in ((width - 256) / 2 + 16)..((width - 256) / 2) + 240
                && mouseY.toInt() in ((height - 256) / 2 + 16)..((height - 256) / 2) + 240
            ) {
                if (shiftX + deltaX > 250)
                    shiftX = 250.0
                else if (shiftX + deltaX < -250)
                    shiftX = -250.0
                else
                    shiftX += deltaX

                if (shiftY + deltaY > 250)
                    shiftY = 250.0
                else if (shiftY + deltaY < -250)
                    shiftY = -250.0
                else
                    shiftY += deltaY
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (keyCode == 256 && shouldCloseOnEsc()) {
            if (activeConfirmScreen == null && activeInfoScreen == null)
                close()
            activeConfirmScreen = null
            activeInfoScreen = null
            true
        } else if (keyCode == 258) {
            val bl = !hasShiftDown()
            if (!changeFocus(bl)) {
                changeFocus(bl)
            }
            false
        } else {
            super.keyPressed(keyCode, scanCode, modifiers)
        }
    }

    override fun tick() {
        frame = (frame + 1) % 1000
        super.tick()
    }
}