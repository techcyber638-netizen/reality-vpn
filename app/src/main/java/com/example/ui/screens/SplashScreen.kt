package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        delay(1500)
        onSplashFinished()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        ),
        label = "spin"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CyberDarkBg,
                        Color(0xFF070B14)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer rotating ring
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 3.dp.toPx()
                    drawArc(
                        color = CyberCyan,
                        startAngle = rotation,
                        sweepAngle = 140f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = CyberPurple,
                        startAngle = rotation + 180f,
                        sweepAngle = 100f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                }

                // Inner pulsing shield
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Cyber Tech Logo",
                    tint = CyberCyan,
                    modifier = Modifier
                        .size(80.dp)
                        .scale(pulseScale)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "CYBER TECH",
                color = CyberTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Secure. Fast. Unblocked.",
                color = CyberCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "VLESS REALITY CORE • XRAY v1.8.4",
                color = CyberTextMuted,
                fontSize = 11.sp,
                letterSpacing = 2.sp
            )
        }
    }
}
