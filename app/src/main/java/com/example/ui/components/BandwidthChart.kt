package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.SpeedSample
import com.example.ui.theme.*

@Composable
fun LiveSpeedChart(
    samples: List<SpeedSample>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CyberCardBg)
            .padding(8.dp)
    ) {
        if (samples.size < 2) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Awaiting Live Traffic Data...",
                    color = CyberTextMuted,
                    fontSize = 12.sp
                )
            }
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                val maxDl = samples.maxOfOrNull { it.downloadMbps }?.coerceAtLeast(10.0) ?: 10.0
                val stepX = width / (samples.size - 1).coerceAtLeast(1)

                // Grid background lines
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = height * (i.toFloat() / gridLines)
                    drawLine(
                        color = CyberCardBorder.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                // Download Path
                val dlPath = Path()
                val dlFillPath = Path()
                dlFillPath.moveTo(0f, height)

                samples.forEachIndexed { index, sample ->
                    val x = index * stepX
                    val y = height - ((sample.downloadMbps / maxDl) * (height * 0.8f)).toFloat()

                    if (index == 0) {
                        dlPath.moveTo(x, y)
                        dlFillPath.lineTo(x, y)
                    } else {
                        val prevX = (index - 1) * stepX
                        val prevY = height - ((samples[index - 1].downloadMbps / maxDl) * (height * 0.8f)).toFloat()
                        val controlX = (prevX + x) / 2
                        dlPath.cubicTo(controlX, prevY, controlX, y, x, y)
                        dlFillPath.cubicTo(controlX, prevY, controlX, y, x, y)
                    }
                }
                dlFillPath.lineTo(width, height)
                dlFillPath.close()

                // Draw gradient fill under download line
                drawPath(
                    path = dlFillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            CyberCyan.copy(alpha = 0.35f),
                            CyberCyan.copy(alpha = 0.0f)
                        )
                    )
                )

                // Draw download stroke line
                drawPath(
                    path = dlPath,
                    color = CyberCyan,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun UsageBarChart(
    weeklyDataMb: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    val maxVal = weeklyDataMb.maxOfOrNull { it.second }?.coerceAtLeast(100.0) ?: 100.0

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            weeklyDataMb.forEach { (day, mb) ->
                val barRatio = (mb / maxVal).toFloat().coerceIn(0.05f, 1f)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (mb >= 1024) String.format("%.1fG", mb / 1024) else "${mb.toInt()}M",
                        color = CyberCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .fillMaxHeight(barRatio)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        CyberCyan,
                                        CyberPurple
                                    )
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = day,
                        color = CyberTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
