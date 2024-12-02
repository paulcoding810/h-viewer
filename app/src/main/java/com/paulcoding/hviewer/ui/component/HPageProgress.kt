package com.paulcoding.hviewer.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun HPageProgress(currentPage: Int, totalPage: Int) {
    Text("$currentPage/$totalPage", fontSize = 10.sp)
}