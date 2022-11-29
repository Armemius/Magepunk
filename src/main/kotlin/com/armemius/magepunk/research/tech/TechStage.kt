package com.armemius.magepunk.research.tech

import com.armemius.magepunk.research.tech.callbacks.Callback
import net.minecraft.item.ItemStack

class TechStage(val reqs: MutableList<TechRequirement>) {
    fun size() = reqs.size
    // Checks callback types of sender and receiver, returns true if they match else false
    fun checkType(cb: Callback, index: Int): Boolean = if (index in 0 until size()) reqs[index].type == cb.type else false
    fun sendCallback(cb: Callback, index: Int, old: Float): Float {
        if (index in 0 until size()) {
            return reqs[index].process(cb, old)
        }
        return 0f
    }

    fun checkIfReady(item: ItemStack): Boolean {
        for (it in 0 until size()) {
            if (item.nbt!!.getFloat("mgp.req.slot$it") != 1f)
                return false
        }
        return true
    }
}