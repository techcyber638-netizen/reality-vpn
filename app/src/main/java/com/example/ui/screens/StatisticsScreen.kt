package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.UsageBarChart
import com.example.ui.theme.*
import com.example.viewmodel.VpnViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: VpnViewModel,
    modifier: Modifier = Modifier
) {
    val todayDlMb by viewModel.todayDownloadMb.collectAsState()
    val todayUlMb by viewModel.todayUploadMb.collectAsState()
    val totalStats by viewModel.totalStats.collectAsState()
    val recentLogs by viewModel.recentLogs.collectAsState()

    val totalDlMb = (totalStats?.totalDownloadMb ?: 0.0) + todayDlMb
    val totalUlMb = (totalStats?.totalUploadMb ?: 0.0) + todayUlMb

    val mockWeeklyData = listOf(
        "Mon" to 420.0,
        "Tue" to 850.0,
        "Wed" to 310.0,
        "Thu" to 1250.0,
        "Fri" to 980.0,
        "Sat" to 1420.0,
        "Sun" to (todayDlMb + todayUlMb)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "BANDWIDTH & STATS",
                        color = CyberTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDarkBg)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Lifetime Stats Summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlassCard(
                        modifier = Modifier.weight(1f),
                        borderColor = CyberCyan
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = "Total Download", tint = CyberCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("TOTAL DOWNLOAD", color = CyberTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (totalDlMb >= 1024) String.format(Locale.US, "%.2f GB", totalDlMb / 1024) else String.format(Locale.US, "%.1f MB", totalDlMb),
                                color = CyberCyan,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    GlassCard(
                        modifier = Modifier.weight(1f),
                        borderColor = CyberPurple
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = "Total Upload", tint = CyberPurple, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("TOTAL UPLOAD", color = CyberTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (totalUlMb >= 1024) String.format(Locale.US, "%.2f GB", totalUlMb / 1024) else String.format(Locale.US, "%.1f MB", totalUlMb),
                                color = CyberPurple,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Weekly Summary Bar Chart
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "WEEKLY CONSUMPTION",
                                color = CyberTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Icon(Icons.Default.DataUsage, contentDescription = "Usage", tint = CyberCyan, modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        UsageBarChart(weeklyDataMb = mockWeeklyData)
                    }
                }
            }

            // Connection History Log
            item {
                Text(
                    text = "CONNECTION HISTORY",
                    color = CyberTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            if (recentLogs.isEmpty()) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Icon(Icons.Default.History, contentDescription = "History", tint = CyberTextMuted)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("No recent connection logs yet.", color = CyberTextMuted, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(recentLogs) { log ->
                    val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.US).format(Date(log.timestamp))
                    val mins = log.durationSeconds / 60
                    val secs = log.durationSeconds % 60

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = log.serverName,
                                    color = CyberTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$dateStr • Protocol: ${log.protocol}",
                                    color = CyberTextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${mins}m ${secs}s",
                                    color = CyberCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = String.format(Locale.US, "↓ %.1fMB  ↑ %.1fMB", log.downloadMb, log.uploadMb),
                                    color = CyberTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
