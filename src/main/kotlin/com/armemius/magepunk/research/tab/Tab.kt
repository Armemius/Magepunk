package com.armemius.magepunk.research.tab

import com.armemius.magepunk.Magepunk
import com.armemius.magepunk.registries.ItemsRegistry
import com.armemius.magepunk.research.tech.Tech
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.Identifier

open class Tab(val name: String, val icon: Item, val background: Identifier, val techs: List<Tech>) {
    open fun isVisible(player: PlayerEntity): Boolean = true

    // List of all tabs
    companion object {
        val TEST = Tab(
            "tab.mgp.test",
            Items.ANVIL,
            Identifier(Magepunk.ID, "textures/gui/research/background/test.png"),
            listOf(
                Tech.TEST_1,
                Tech.TEST_2,
                Tech.TEST_3
            )
        )
        val BASICS = Tab(
            "tab.mgp.basics",
            ItemsRegistry.ENCHIRIDION,
            Identifier(Magepunk.ID, "textures/gui/research/background/general.png"),
            listOf()
        )
    }

}