package com.armemius.magepunk.research.tech

import com.armemius.magepunk.research.tech.callbacks.Callback
import com.armemius.magepunk.research.tech.callbacks.CallbackType

class TechRequirement(
    val id: String,
    val type: CallbackType,
    // Takes callback and saved float value and returns new float value to overwrite old
    private val processor: (Callback, Float) -> Float
) {
    fun process(cb: Callback, old: Float): Float {
        if (old >= 1f)
            return 1f
        var res = processor(cb, old)
        return minOf(res, 1f)
    }
}