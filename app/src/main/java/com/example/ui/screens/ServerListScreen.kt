package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VpnServer
import com.example.ui.components.GlassCard
import com.example.ui.components.PingBadge
import com.example.ui.theme.*
import com.example.utils.VpnConfigParser
import com.example.viewmodel.VpnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerListScreen(
    viewModel: VpnViewModel,
    onNavigateToAddConfig: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val serverList by viewModel.servers.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val isPingingAll by viewModel.isPingingAll.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedProtocolFilter by remember { mutableStateOf("ALL") }

    val filteredServers = remember(serverList, searchQuery, selectedProtocolFilter) {
        serverList.filter { server ->
            val matchesSearch = server.name.contains(searchQuery, ignoreCase = true) ||
                    server.serverAddress.contains(searchQuery, ignoreCase = true) ||
                    server.sni.contains(searchQuery, ignoreCase = true)

            val matchesProtocol = when (selectedProtocolFilter) {
                "ALL" -> true
                else -> server.protocol.equals(selectedProtocolFilter, ignoreCase = true)
            }

            matchesSearch && matchesProtocol
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SERVER NODES",
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
                actions = {
                    IconButton(onClick = { viewModel.pingAllServers() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Ping All Nodes",
                            tint = if (isPingingAll) CyberYellow else CyberCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDarkBg)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddConfig,
                containerColor = CyberCyan,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier.testTag("add_server_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add New Node",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("server_search_field"),
                placeholder = { Text("Search by name, IP, or SNI...", color = CyberTextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = CyberCyan) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = CyberTextMuted)
                        }
                    }
                },
                singleLine = true,
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

            // Protocol Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "VLESS", "VMess", "Trojan", "SS").forEach { proto ->
                    val isSelected = selectedProtocolFilter == proto
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedProtocolFilter = proto },
                        label = {
                            Text(
                                text = proto,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = CyberCardBg,
                            labelColor = CyberTextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredServers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "No Servers",
                            tint = CyberTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No proxy configurations found.",
                            color = CyberTextSecondary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap + to add VLESS Reality, VMess or Trojan node.",
                            color = CyberTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredServers, key = { it.id }) { server ->
                        val isSelected = selectedServer?.id == server.id

                        GlassCard(
                            borderColor = if (isSelected) CyberCyan else CyberCardBorder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectServer(server)
                                }
                                .testTag("server_item_${server.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.selectServer(server) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = CyberCyan,
                                        unselectedColor = CyberTextMuted
                                    )
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = server.name,
                                            color = CyberTextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        if (server.isFavorite) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Favorite",
                                                tint = CyberYellow,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = "${server.serverAddress}:${server.port} • SNI: ${server.sni.ifEmpty { "None" }}",
                                        color = CyberTextSecondary,
                                        fontSize = 12.sp
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CyberPurple.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (server.security == "reality") "VLESS REALITY" else server.protocol,
                                                color = CyberPurple,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        if (server.fingerprint.isNotEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(CyberCyan.copy(alpha = 0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "uTLS: ${server.fingerprint}",
                                                    color = CyberCyan,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    PingBadge(
                                        pingMs = server.pingMs,
                                        modifier = Modifier.clickable { viewModel.pingServer(server) }
                                    )

                                    Row {
                                        IconButton(
                                            onClick = {
                                                val shareUrl = VpnConfigParser.generateShareLink(server)
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("VPN Config", shareUrl))
                                                Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy Link",
                                                tint = CyberTextMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteServer(server) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete Node",
                                                tint = CyberMagenta,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
