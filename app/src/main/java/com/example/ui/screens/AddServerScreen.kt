package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VpnServer
import com.example.engine.VpnEngine
import com.example.ui.components.CyberActionButton
import com.example.ui.components.GlassCard
import com.example.ui.components.PingBadge
import com.example.ui.theme.*
import com.example.utils.VpnConfigParser
import com.example.viewmodel.VpnViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServerScreen(
    viewModel: VpnViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) } // 0: Import Link, 1: QR Scan, 2: Manual Form

    // Import Link state
    var shareLinkInput by remember { mutableStateOf("") }

    // Manual Form states
    var name by remember { mutableStateOf("US - Reality Custom Node") }
    var protocol by remember { mutableStateOf("VLESS") }
    var serverAddress by remember { mutableStateOf("104.28.19.44") }
    var port by remember { mutableStateOf("443") }
    var uuid by remember { mutableStateOf("e7492a91-5364-4e2a-9f86-2a7bd1c6e112") }
    var security by remember { mutableStateOf("reality") }
    var sni by remember { mutableStateOf("discord.com") }
    var fingerprint by remember { mutableStateOf("chrome") }
    var publicKey by remember { mutableStateOf("k9Zbv89P0LmX4NqRsTUvwXyZaBcDeFgHiJkLmNoPqRs") }
    var shortId by remember { mutableStateOf("6ba7b810") }
    var flow by remember { mutableStateOf("xtls-rprx-vision") }
    var network by remember { mutableStateOf("tcp") }

    var testPingResult by remember { mutableStateOf(-1) }
    var isTestingPing by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ADD PROXY NODE",
                        color = CyberTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = CyberCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDarkBg)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Header
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CyberCardBg,
                contentColor = CyberCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CyberCyan
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("PASTE LINK", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.ContentPaste, contentDescription = "Paste Link", modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("QR SCAN", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "QR Scanner", modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("MANUAL ENTRY", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Manual Form", modifier = Modifier.size(18.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // Paste Link Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text(
                                    text = "Import Share Link",
                                    color = CyberTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Supports vless://, vmess://, trojan://, ss:// URIs",
                                    color = CyberTextSecondary,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = shareLinkInput,
                                    onValueChange = { shareLinkInput = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .testTag("share_link_text_input"),
                                    placeholder = {
                                        Text(
                                            "vless://e7492a91-5364...@104.28.19.44:443?type=tcp&security=reality...",
                                            color = CyberTextMuted,
                                            fontSize = 12.sp
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyberCyan,
                                        unfocusedBorderColor = CyberCardBorder,
                                        focusedContainerColor = CyberCardBg,
                                        unfocusedContainerColor = CyberCardBg,
                                        focusedTextColor = CyberTextPrimary,
                                        unfocusedTextColor = CyberTextPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    CyberActionButton(
                                        text = "Paste Clipboard",
                                        icon = Icons.Default.ContentPaste,
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clipData = clipboard.primaryClip
                                            if (clipData != null && clipData.itemCount > 0) {
                                                val pasted = clipData.getItemAt(0).text?.toString() ?: ""
                                                shareLinkInput = pasted
                                            } else {
                                                Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        accentColor = CyberPurple
                                    )

                                    CyberActionButton(
                                        text = "Import Config",
                                        icon = Icons.Default.Check,
                                        onClick = {
                                            if (shareLinkInput.isBlank()) {
                                                Toast.makeText(context, "Please enter a config link", Toast.LENGTH_SHORT).show()
                                            } else {
                                                val success = viewModel.importFromLink(shareLinkInput)
                                                if (success) {
                                                    Toast.makeText(context, "Node imported successfully!", Toast.LENGTH_SHORT).show()
                                                    onBack()
                                                } else {
                                                    Toast.makeText(context, "Invalid URI scheme or format", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        accentColor = CyberCyan
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Simulated QR Scanner Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp),
                            borderColor = CyberCyan
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "QR Scanner View",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(80.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "ALIGN QR CODE INSIDE FRAME",
                                    color = CyberTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Scanning VLESS Reality QR code...",
                                    color = CyberTextSecondary,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                CyberActionButton(
                                    text = "Simulate QR Import",
                                    icon = Icons.Default.CameraAlt,
                                    onClick = {
                                        val mockVless = "vless://e7492a91-5364-4e2a-9f86-2a7bd1c6e112@104.28.19.44:443?type=tcp&security=reality&pbk=k9Zbv89P0LmX4NqRsTUvwXyZaBcDeFgHiJkLmNoPqRs&fp=chrome&sni=discord.com&sid=6ba7b810&flow=xtls-rprx-vision#US-QR-Scanned-Node"
                                        viewModel.importFromLink(mockVless)
                                        Toast.makeText(context, "QR Code scanned! Config imported.", Toast.LENGTH_SHORT).show()
                                        onBack()
                                    },
                                    accentColor = CyberCyan
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Manual Form Tab
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "VLESS REALITY MANUAL CONFIG",
                                    color = CyberCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )

                                CustomTextField("Node Name", name) { name = it }

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Protocol", color = CyberTextSecondary, fontSize = 11.sp)
                                        Row {
                                            listOf("VLESS", "VMess", "Trojan").forEach { p ->
                                                FilterChip(
                                                    selected = protocol == p,
                                                    onClick = { protocol = p },
                                                    label = { Text(p, fontSize = 10.sp) },
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = CyberCyan,
                                                        selectedLabelColor = Color.Black
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                        }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(modifier = Modifier.weight(2f)) {
                                        CustomTextField("Server Address / IP", serverAddress) { serverAddress = it }
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        CustomTextField("Port", port) { port = it }
                                    }
                                }

                                CustomTextField("UUID / User ID", uuid) { uuid = it }

                                Divider(color = CyberCardBorder, thickness = 1.dp)

                                Text(
                                    text = "REALITY TUNNEL PARAMETERS",
                                    color = CyberPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )

                                CustomTextField("SNI Masquerading Host", sni) { sni = it }
                                CustomTextField("Public Key (pbk)", publicKey) { publicKey = it }

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        CustomTextField("Short ID (sid)", shortId) { shortId = it }
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        CustomTextField("Flow", flow) { flow = it }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("uTLS Fingerprint", color = CyberTextSecondary, fontSize = 11.sp)
                                        Row {
                                            listOf("chrome", "firefox", "safari").forEach { fp ->
                                                FilterChip(
                                                    selected = fingerprint == fp,
                                                    onClick = { fingerprint = fp },
                                                    label = { Text(fp, fontSize = 10.sp) },
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = CyberPurple,
                                                        selectedLabelColor = Color.White
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                        }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Transport Network", color = CyberTextSecondary, fontSize = 11.sp)
                                        Row {
                                            listOf("tcp", "ws", "gRPC").forEach { net ->
                                                FilterChip(
                                                    selected = network == net,
                                                    onClick = { network = net },
                                                    label = { Text(net, fontSize = 10.sp) },
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = CyberCyan,
                                                        selectedLabelColor = Color.Black
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CyberActionButton(
                                        text = if (isTestingPing) "Pinging..." else "Test Latency",
                                        icon = Icons.Default.Speed,
                                        onClick = {
                                            coroutineScope.launch {
                                                isTestingPing = true
                                                val res = VpnEngine.pingServer(serverAddress)
                                                testPingResult = res
                                                isTestingPing = false
                                            }
                                        },
                                        accentColor = CyberYellow
                                    )

                                    if (testPingResult >= 0) {
                                        PingBadge(pingMs = testPingResult)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                CyberActionButton(
                                    text = "SAVE PROXY CONFIGURATION",
                                    icon = Icons.Default.Save,
                                    onClick = {
                                        val portInt = port.toIntOrNull() ?: 443
                                        val server = VpnServer(
                                            name = name.ifEmpty { "Manual Node" },
                                            protocol = protocol,
                                            serverAddress = serverAddress,
                                            port = portInt,
                                            uuid = uuid,
                                            flow = flow,
                                            network = network,
                                            security = security,
                                            sni = sni,
                                            fingerprint = fingerprint,
                                            publicKey = publicKey,
                                            shortId = shortId,
                                            pingMs = testPingResult
                                        )

                                        viewModel.addServer(server)
                                        Toast.makeText(context, "Server saved successfully!", Toast.LENGTH_SHORT).show()
                                        onBack()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    accentColor = CyberCyan
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(text = label, color = CyberTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = CyberCardBorder,
                focusedContainerColor = CyberCardBg,
                unfocusedContainerColor = CyberCardBg,
                focusedTextColor = CyberTextPrimary,
                unfocusedTextColor = CyberTextPrimary
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}
