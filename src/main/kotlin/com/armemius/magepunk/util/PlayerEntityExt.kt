package com.armemius.magepunk.util

import com.armemius.magepunk.research.tech.callbacks.Callback

interface PlayerEntityExt {
    fun getResearchPts(): Int
    fun setResearchPts(value: Int): Unit
    fun addResearchPts(value: Int): Unit
    fun removeResearchPts(value: Int): Boolean
    fun sendCallback(cb: Callback)
}