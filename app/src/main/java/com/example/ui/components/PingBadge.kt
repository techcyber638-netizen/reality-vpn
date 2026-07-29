package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberMagenta
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberYellow

@Composable
fun PingBadge(
    pingMs: Int,
    modifier: Modifier = Modifier
) {
    val (color, text) = when {
        pingMs < 0 -> Pair(Color.Gray, "N/A")
        pingMs < 100 -> Pair(CyberGreen, "${pingMs}ms")
        pingMs < 250 -> Pair(CyberYellow, "${pingMs}ms")
        else -> Pair(CyberMagenta, "${pingMs}ms")
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = if (pingMs < 0) CyberTextSecondary else color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
