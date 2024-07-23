@file:OptIn(ExperimentalMaterial3Api::class)

package com.guyuuan.app.find_author.core.ui.compoments

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * @author: Chen
 * @createTime: 6/11/24 11:21
 * @description:
 **/

@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    content: @Composable (PaddingValues, NavHostController) -> Unit
) {
//    val currentStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute =
//        AppRoute.entries.find { it.route == currentStackEntry?.destination?.route } ?: AppRoute.HOME
//    Scaffold(modifier = modifier, topBar = {
//        AppBar(
//            visible = currentRoute.showAppBar,
//            canBack = navController.previousBackStackEntry != null
//        ) {
//            navController.navigateUp()
//        }
//    }) {
        content(PaddingValues(0.dp), navController)
//    }
}

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    visible: Boolean,
    canBack: Boolean,
    navigateUp: () -> Unit
) {
    if (!visible) return
    TopAppBar(modifier = modifier, title = { title?.let { Text(text = it) } }, navigationIcon = {
        if (canBack) IconButton(onClick = navigateUp) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null
            )
        }
    })
}