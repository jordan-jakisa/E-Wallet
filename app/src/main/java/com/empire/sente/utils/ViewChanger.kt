package com.empire.sente.utils

import androidx.navigation.NavDirections

interface ViewChanger {
    fun fragmentTransaction(action: NavDirections)
    abstract fun globalFragmentTransaction(actionId: Int)
}