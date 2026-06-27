package com.lifetracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Todos : BottomNavItem("todos", Icons.Default.List, "待办")
    object Countdowns : BottomNavItem("countdowns", Icons.Default.DateRange, "倒计时")
    object CheckIns : BottomNavItem("checkins", Icons.Default.CheckCircle, "打卡")
}
