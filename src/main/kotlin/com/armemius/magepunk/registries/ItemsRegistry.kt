package com.armemius.magepunk.registries

import com.armemius.magepunk.Magepunk
import com.armemius.magepunk.objects.item.*
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry


object ItemsRegistry {
    val RESEARCH: Item = ResearchItem()
    val ENCHIRIDION: Item = EnchiridionItem()
    val INKWELL_WITH_FEATHER: Item = InkwellWithFeatherItem()

    fun register() {
        Magepunk.log("Items registry")
        registerItem(RESEARCH, "research")
        registerItem(ENCHIRIDION, "enchiridion")
        registerItem(INKWELL_WITH_FEATHER, "inkwell_with_feather")
    }

    private fun registerItem(item: Item, name: String) =
        Registry.register(Registry.ITEM, Identifier(Magepunk.ID, name), item)
}