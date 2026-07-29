package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ConnectionState
import com.example.ui.theme.*

@Composable
fun CyberConnectButton(
    connectionState: ConnectionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    val infiniteTransition = rememberInfiniteTransition(label = "cyber_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    val buttonColor = when (connectionState) {
        ConnectionState.CONNECTED -> CyberGreen
        ConnectionState.CONNECTING, ConnectionState.RECONNECTING -> CyberYellow
        ConnectionState.DISCONNECTING -> CyberMagenta
        ConnectionState.DISCONNECTED -> CyberCyan
    }

    val glowColor = buttonColor.copy(alpha = 0.35f)

    Box(
        modifier = modifier
            .size(180.dp)
            .testTag("cyber_connect_button"),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing ring when connecting/connected
        if (connectionState == ConnectionState.CONNECTED || connectionState == ConnectionState.CONNECTING) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(glowColor)
            )
        }

        // Concentric tech ring canvas
        Canvas(modifier = Modifier.size(160.dp)) {
            val strokeWidth = 3.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2

            // Background subtle ring
            drawCircle(
                color = buttonColor.copy(alpha = 0.2f),
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // Dashed or rotating tech accents
            drawArc(
                color = buttonColor,
                startAngle = rotation,
                sweepAngle = 120f,
                useCenter = false,
                style = Stroke(width = strokeWidth + 2f)
            )
            drawArc(
                color = buttonColor.copy(alpha = 0.6f),
                startAngle = rotation + 180f,
                sweepAngle = 90f,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
        }

        // Inner Power Button
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(16.dp, CircleShape, spotColor = buttonColor, ambientColor = buttonColor)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            buttonColor.copy(alpha = 0.25f),
                            CyberCardBg
                        )
                    )
                )
                .border(2.dp, buttonColor, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "Power Connection",
                    tint = buttonColor,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (connectionState) {
                        ConnectionState.CONNECTED -> "STOP"
                        ConnectionState.CONNECTING -> "WAIT..."
                        ConnectionState.DISCONNECTING -> "STOPPING"
                        ConnectionState.RECONNECTING -> "RETRY"
                        ConnectionState.DISCONNECTED -> "START"
                    },
                    color = buttonColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun CyberActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = CyberCyan,
    enabled: Boolean = true
) {
    val view = LocalView.current

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CyberCardBg)
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = accentColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = CyberTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
