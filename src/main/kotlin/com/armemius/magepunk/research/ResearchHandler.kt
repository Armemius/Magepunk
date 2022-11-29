package com.armemius.magepunk.research

import com.armemius.magepunk.research.tab.Tab
import com.armemius.magepunk.research.tech.Tech
import com.armemius.magepunk.util.IPlayerDataManager
import net.minecraft.entity.player.PlayerEntity

object ResearchHandler {
    private var tabs: MutableList<Tab> = mutableListOf()

    fun registerTab(tab: Tab) = tabs.add(tab)

    fun registerTab(tab: Tab, index: Int) = tabs.add(index, tab)

    fun getAvailableTabs(player: PlayerEntity): List<Tab> {
        var raw: MutableList<Tab> = mutableListOf()
        tabs.forEach {
            if (it.isVisible(player))
                raw.add(it)
        }
        return raw
    }

    fun PlayerEntity.requireTechs(tech: Tech, vararg techs: Tech): Boolean {
        var player = this as IPlayerDataManager
        var nbt = player.getTechsNbt()
        var nbtTag = "tech.mgp.data.${tech.name}"
        if (nbt.contains(nbtTag)) {
            if (!nbt.getBoolean(nbtTag))
                return false
            for (it in techs) {
                nbtTag = "tech.mgp.data.${it.name}"
                if (nbt.contains(nbtTag)) {
                    if (!nbt.getBoolean(nbtTag))
                        return false
                }
                else
                    return false
            }
            return true
        }
        return false
    }
}