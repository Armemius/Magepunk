package com.armemius.magepunk.registries

import com.armemius.magepunk.Magepunk
import com.armemius.magepunk.research.ResearchHandler
import com.armemius.magepunk.research.tab.Tab

object TabsRegistry {
    fun register() {
        Magepunk.log("Tabs registry")
        ResearchHandler.registerTab(Tab.TEST)
        ResearchHandler.registerTab(Tab.BASICS)
    }
}