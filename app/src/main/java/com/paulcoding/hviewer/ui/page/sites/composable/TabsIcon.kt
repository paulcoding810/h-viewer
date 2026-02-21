package com.paulcoding.hviewer.ui.page.sites.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.paulcoding.hviewer.ui.component.HIcon

@Composable
fun TabsIcon(
    onGloballyPositioned: (Offset) -> Unit = {},
    size: Int = 0,
    onClick: () -> Unit = {},
) {
    Box(modifier = Modifier
        .onGloballyPositioned {
            onGloballyPositioned(it.positionInRoot())
        }) {
        if (size != 0) {
            HIcon(
                imageVector = Icons.Outlined.Tab,
            ) { onClick() }
            Text(
                size.toString(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(CircleShape),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}