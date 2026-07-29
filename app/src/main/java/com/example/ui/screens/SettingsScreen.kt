package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.DnsMode
import com.example.engine.EngineType
import com.example.engine.RoutingRule
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.VpnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: VpnViewModel,
    onOpenLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedEngine by viewModel.engineType.collectAsState()
    val selectedDns by viewModel.dnsMode.collectAsState()
    val selectedRouting by viewModel.routingRule.collectAsState()
    val autoReconnect by viewModel.autoReconnect.collectAsState()
    val killSwitch by viewModel.killSwitch.collectAsState()

    var selectedLanguage by remember { mutableStateOf("English (US)") }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SETTINGS",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Core Tunnel Engine Selector
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingsSectionHeader("CORE PROXY ENGINE", Icons.Default.Memory)
                    Spacer(modifier = Modifier.height(10.dp))

                    EngineType.values().forEach { engine ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedEngine == engine,
                                onClick = { viewModel.engineType.value = engine },
                                colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${engine.displayName} (${engine.version})",
                                    color = CyberTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (engine == EngineType.XRAY) "Full VLESS Reality, Vision, uTLS, gRPC, WebSocket" else "Modern universal proxy core",
                                    color = CyberTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // DNS Settings
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingsSectionHeader("DNS RESOLUTION", Icons.Default.Dns)
                    Spacer(modifier = Modifier.height(10.dp))

                    DnsMode.values().forEach { dns ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedDns == dns,
                                onClick = { viewModel.dnsMode.value = dns },
                                colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = dns.displayName,
                                    color = CyberTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Primary: ${dns.primary} • Secondary: ${dns.secondary}",
                                    color = CyberTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Routing Mode
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingsSectionHeader("ROUTING RULES", Icons.Default.AltRoute)
                    Spacer(modifier = Modifier.height(10.dp))

                    RoutingRule.values().forEach { rule ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedRouting == rule,
                                onClick = { viewModel.routingRule.value = rule },
                                colors = RadioButtonDefaults.colors(selectedColor = CyberPurple)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = rule.displayName,
                                    color = CyberTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = rule.description,
                                    color = CyberTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Connection Options (Auto-Reconnect & Kill Switch)
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingsSectionHeader("TUNNEL BEHAVIOR", Icons.Default.Shield)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Auto-Reconnect", color = CyberTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Automatically reconnect if connection drops", color = CyberTextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = autoReconnect,
                            onCheckedChange = { viewModel.autoReconnect.value = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = CyberCyan.copy(alpha = 0.3f))
                        )
                    }

                    Divider(color = CyberCardBorder, thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Kill Switch", color = CyberTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Block internet traffic when VPN drops", color = CyberTextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = killSwitch,
                            onCheckedChange = { viewModel.killSwitch.value = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberMagenta, checkedTrackColor = CyberMagenta.copy(alpha = 0.3f))
                        )
                    }
                }
            }

            // Console Logs Trigger
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyberCyan.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Live Core Log Console", color = CyberTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("View real-time Xray-core handshake & traffic logs", color = CyberTextSecondary, fontSize = 11.sp)
                    }
                    Button(
                        onClick = onOpenLogs,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = SystemTextColor)
                    ) {
                        Text("Open Logs", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // About Cyber Tech Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Shield, contentDescription = "Logo", tint = CyberCyan, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("CYBER TECH VPN", color = CyberTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Text("Version 2.4.0 • Build 2026", color = CyberTextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Secure. Fast. Unblocked.", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

private val SystemTextColor = Color(0xFF000000)

@Composable
private fun SettingsSectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = title, tint = CyberCyan, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}
