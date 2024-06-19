package com.guyuuan.app.find_author.core.ui.locals

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

/**
 * @author: Chen
 * @createTime: 6/11/24 11:19
 * @description:
 **/

val LocalNavController = staticCompositionLocalOf<NavController> {
    noLocalProvidedFor("NavController")
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}