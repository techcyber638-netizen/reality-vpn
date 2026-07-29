package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ConnectionState
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.VpnViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: VpnViewModel,
    onNavigateToServerList: () -> Unit,
    onNavigateToAddConfig: () -> Unit,
    onOpenLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val downloadSpeed by viewModel.downloadSpeed.collectAsState()
    val uploadSpeed by viewModel.uploadSpeed.collectAsState()
    val sessionDuration by viewModel.sessionDurationSeconds.collectAsState()
    val todayDlMb by viewModel.todayDownloadMb.collectAsState()
    val todayUlMb by viewModel.todayUploadMb.collectAsState()
    val speedHistory by viewModel.speedHistory.collectAsState()
    val isPingingAll by viewModel.isPingingAll.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CYBER TECH",
                        color = CyberTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "VLESS REALITY PROT",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(CyberCardBg)
                            .clickable { viewModel.pingAllServers() }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NetworkCheck,
                            contentDescription = "Ping All Servers",
                            tint = if (isPingingAll) CyberYellow else CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(CyberCardBg)
                            .clickable { onOpenLogs() }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "View Logs",
                            tint = CyberTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Active Server Card
        item {
            GlassCard(
                borderColor = if (connectionState == ConnectionState.CONNECTED) CyberGreen else CyberCyan,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToServerList() }
                    .testTag("active_server_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = "Server Node",
                                tint = CyberCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = selectedServer?.name ?: "Select a Server",
                                color = CyberTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${selectedServer?.protocol ?: "VLESS"} • SNI: ${selectedServer?.sni ?: "discord.com"}",
                                    color = CyberTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        PingBadge(pingMs = selectedServer?.pingMs ?: -1)
                        Spacer(modifier = Modifier.height(4.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Change Server",
                            tint = CyberTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Central Power Connect Button
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = androidx.compose.ui.platform.LocalContext.current
                CyberConnectButton(
                    connectionState = connectionState,
                    onClick = { viewModel.toggleConnect(context) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Connection Status Label & Timer
                val statusText = when (connectionState) {
                    ConnectionState.CONNECTED -> "CONNECTED • EXCELLENT TUNNEL"
                    ConnectionState.CONNECTING -> "ESTABLISHING VLESS REALITY..."
                    ConnectionState.DISCONNECTING -> "DISCONNECTING TUNNEL..."
                    ConnectionState.RECONNECTING -> "RECONNECTING PROXY..."
                    ConnectionState.DISCONNECTED -> "DISCONNECTED • TAP TO CONNECT"
                }

                val statusColor = when (connectionState) {
                    ConnectionState.CONNECTED -> CyberGreen
                    ConnectionState.CONNECTING, ConnectionState.RECONNECTING -> CyberYellow
                    ConnectionState.DISCONNECTING -> CyberMagenta
                    ConnectionState.DISCONNECTED -> CyberTextSecondary
                }

                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                if (connectionState == ConnectionState.CONNECTED) {
                    val hours = sessionDuration / 3600
                    val minutes = (sessionDuration % 3600) / 60
                    val seconds = sessionDuration % 60
                    val timerFormatted = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Duration: $timerFormatted",
                        color = CyberCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Real-time Speed Gauges
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassCard(
                    modifier = Modifier.weight(1f),
                    borderColor = CyberCyan.copy(alpha = 0.4f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Download Speed",
                            tint = CyberCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DOWNLOAD",
                                color = CyberTextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f Mbps", downloadSpeed),
                                color = CyberTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                GlassCard(
                    modifier = Modifier.weight(1f),
                    borderColor = CyberPurple.copy(alpha = 0.4f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Upload Speed",
                            tint = CyberPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "UPLOAD",
                                color = CyberTextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f Mbps", uploadSpeed),
                                color = CyberTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Bandwidth Live Graph
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE BANDWIDTH MONITOR",
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Today: ${String.format(Locale.US, "%.1f MB", todayDlMb + todayUlMb)}",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LiveSpeedChart(samples = speedHistory)
                }
            }
        }

        // Quick Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CyberActionButton(
                    text = "Import Config",
                    icon = Icons.Default.Add,
                    onClick = onNavigateToAddConfig,
                    modifier = Modifier.weight(1f),
                    accentColor = CyberCyan
                )

                CyberActionButton(
                    text = "Server Nodes",
                    icon = Icons.Default.List,
                    onClick = onNavigateToServerList,
                    modifier = Modifier.weight(1f),
                    accentColor = CyberPurple
                )
            }
        }
    }
}
