package com.armemius.magepunk.research.tech

import com.armemius.magepunk.research.tech.techs.Test1Tech
import com.armemius.magepunk.research.tech.techs.Test2Tech
import com.armemius.magepunk.research.tech.techs.Test3Tech
import com.armemius.magepunk.util.IPlayerDataManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item

abstract class Tech(
    val name: String,
    val icon: Item,
    val tier: Int,
    val diff: Difficulty,
    val shape: Shape,
    val posX: Int,
    val posY: Int,
    var links: List<Tech>,
    var stages: List<TechStage>
) {
    fun isFinished(player: PlayerEntity): Boolean {
        if (isDefault())
            return true
        val nbt = (player as IPlayerDataManager).getTechsNbt()
        if (!nbt.contains("tech.mgp.data.${name}"))
            return false
        return nbt.getBoolean("tech.mgp.data.${name}")
    }

    // Tech can be visible, but still not available to research
    open fun isVisible(player: PlayerEntity): Boolean = true

    open fun isAvailable(player: PlayerEntity): Boolean = true

    open fun isDefault(): Boolean = false

    open fun onFinished(player: PlayerEntity) =
        (player as IPlayerDataManager)
            .getTechsNbt()
            .putBoolean("tech.mgp.data.${name}", true)

    companion object {
        val TEST_1: Tech
        val TEST_2: Tech
        val TEST_3: Tech

        var TECHS: List<Tech>
        var TECHS_MAP: MutableMap<String, Tech>

        init {
            TEST_1 = Test1Tech(listOf())
            TEST_2 = Test2Tech(listOf(TEST_1))
            TEST_3 = Test3Tech(listOf())

            TECHS = listOf(
                TEST_1,
                TEST_2,
                TEST_3
            )

            TECHS_MAP = mutableMapOf()
            for (it in TECHS) {
                TECHS_MAP[it.name] = it
            }
        }

    }
}