package com.armemius.magepunk.research.tech.techs

import com.armemius.magepunk.Magepunk
import com.armemius.magepunk.research.ResearchHandler.requireTechs
import com.armemius.magepunk.research.tech.*
import com.armemius.magepunk.research.tech.callbacks.CallbackPackages
import com.armemius.magepunk.research.tech.callbacks.CallbackType
import com.armemius.magepunk.research.tech.render.PageRenderComponents
import com.armemius.magepunk.research.tech.render.components.ImageRenderComponent
import com.armemius.magepunk.research.tech.render.components.PlainTextRendererComponent
import com.armemius.magepunk.research.tech.render.components.TitleRenderComponent
import com.armemius.magepunk.research.tech.render.components.WorkbenchCraftingRenderComponent
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.math.sqrt

class Test2Tech(links: List<Tech>): Tech(
    "tech.mgp.test2",
    Items.COMMAND_BLOCK,
    1,
    Difficulty.EASY,
    Shape.SQUARE,
    40,
    0,
    links,
    listOf(
        TechStage(
            mutableListOf(
                TechRequirement("mgp.req.travel", CallbackType.MOVEMENT) { cb, old ->
                    val mp = cb.packet as CallbackPackages.Movement
                    old + sqrt(mp.dx * mp.dx + mp.dz * mp.dz).toFloat() / 250
                },
                TechRequirement("mgp.req.height", CallbackType.TICK) { cb, old ->
                    if (cb.player.y >= 100)
                        1f
                    else
                        0f
                },
                TechRequirement("mgp.req.depth", CallbackType.TICK) { cb, old ->
                    if (cb.player.y <= -61)
                        1f
                    else
                        0f
                }
            )
        ),
        TechStage(
            mutableListOf(
                TechRequirement("mgp.req.ct_interact", CallbackType.USE_BLOCK) { cb, old ->
                    val mp = cb.packet as CallbackPackages.UseBlock
                    if (mp.world.getBlockState(mp.hitResult.blockPos).block == Blocks.CRAFTING_TABLE)
                        1f
                    else
                        0f
                },
                TechRequirement("mgp.req.crouch", CallbackType.MOVEMENT) { cb, old ->
                    val mp = cb.packet as CallbackPackages.Movement
                    if (mp.isInSneakingPose)
                        old + Math.sqrt(mp.dx * mp.dx + mp.dz * mp.dz).toFloat() / 10
                    else
                        old
                }
            )
        )
    ),
    listOf(
        PageRenderComponents(
            listOf(
                TitleRenderComponent(Text.translatable("tech.mgp.test2"), 12, -16777216),
                PlainTextRendererComponent(Text.translatable("tech.mgp.test2.desc"), 8, -16777216)
            )
        ),
        PageRenderComponents(
            listOf(
                TitleRenderComponent(Text.translatable("tech.mgp.test2"), 12, -16777216),
                PlainTextRendererComponent(Text.of("AMOGUSUS PAGE 2"), 8, -16777216),
                WorkbenchCraftingRenderComponent(Items.IRON_BLOCK.defaultStack,
                    Items.IRON_INGOT.defaultStack, Items.IRON_INGOT.defaultStack, Items.IRON_INGOT.defaultStack,
                    Items.IRON_INGOT.defaultStack, Items.IRON_INGOT.defaultStack, Items.IRON_INGOT.defaultStack,
                    Items.IRON_INGOT.defaultStack, Items.IRON_INGOT.defaultStack, Items.IRON_INGOT.defaultStack)
            )
        ),
        PageRenderComponents(
            listOf(
                TitleRenderComponent(Text.translatable("tech.mgp.test2"), 12, -16777216),
                PlainTextRendererComponent(Text.of("Incredibly sus PAGE 3"), 8, -16777216),
                ImageRenderComponent(Identifier(Magepunk.ID, "textures/gui/research/techs/amogus.png"), 150, 150)
            )
        )
    )
) {
    override fun isAvailable(player: PlayerEntity): Boolean = player.requireTechs(TEST_1)
}