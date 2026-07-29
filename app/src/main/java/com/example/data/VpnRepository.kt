package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class VpnRepository(private val db: AppDatabase) {

    val allServers: Flow<List<VpnServer>> = db.vpnServerDao().getAllServers()
    val recentLogs: Flow<List<ConnectionLog>> = db.connectionLogDao().getRecentLogs()
    val totalStats: Flow<UsageStats> = db.connectionLogDao().getTotalStats()

    suspend fun insertServer(server: VpnServer): Long = db.vpnServerDao().insertServer(server)

    suspend fun updateServer(server: VpnServer) = db.vpnServerDao().updateServer(server)

    suspend fun deleteServer(server: VpnServer) = db.vpnServerDao().deleteServer(server)

    suspend fun deleteServerById(id: Long) = db.vpnServerDao().deleteServerById(id)

    suspend fun updatePing(id: Long, pingMs: Int) = db.vpnServerDao().updatePing(id, pingMs)

    suspend fun toggleFavorite(id: Long, current: Boolean) = db.vpnServerDao().setFavorite(id, !current)

    suspend fun insertConnectionLog(log: ConnectionLog) = db.connectionLogDao().insertLog(log)

    suspend fun clearLogs() = db.connectionLogDao().clearLogs()

    suspend fun seedInitialServersIfEmpty() {
        val current = allServers.first()
        if (current.isEmpty()) {
            val seeds = listOf(
                VpnServer(
                    name = "US - Cyber Reality Alpha",
                    protocol = "VLESS",
                    serverAddress = "104.28.19.44",
                    port = 443,
                    uuid = "e7492a91-5364-4e2a-9f86-2a7bd1c6e112",
                    flow = "xtls-rprx-vision",
                    network = "tcp",
                    security = "reality",
                    sni = "discord.com",
                    fingerprint = "chrome",
                    publicKey = "k9Zbv89P0LmX4NqRsTUvwXyZaBcDeFgHiJkLmNoPqRs",
                    shortId = "6ba7b810",
                    spiderX = "/",
                    pingMs = 42,
                    isFavorite = true
                ),
                VpnServer(
                    name = "DE - Frankfurt Stealth VLESS",
                    protocol = "VLESS",
                    serverAddress = "162.159.135.24",
                    port = 443,
                    uuid = "4d83b1c2-90e1-4b77-8c31-ef12948290fa",
                    flow = "xtls-rprx-vision",
                    network = "tcp",
                    security = "reality",
                    sni = "apple.com",
                    fingerprint = "firefox",
                    publicKey = "pW7xYzAbCdEfGhIjKlMnOpQrStUvWxYz0123456789A",
                    shortId = "1a2b3c4d",
                    spiderX = "/",
                    pingMs = 88,
                    isFavorite = false
                ),
                VpnServer(
                    name = "JP - Tokyo Quantum Reality",
                    protocol = "VLESS",
                    serverAddress = "172.67.182.91",
                    port = 443,
                    uuid = "7c61f234-11a2-4d98-b80c-99a2214300bb",
                    flow = "xtls-rprx-vision",
                    network = "gRPC",
                    security = "reality",
                    sni = "dl.google.com",
                    fingerprint = "safari",
                    publicKey = "X9zY8wV7uT6sR5qP4oN3mL2kJ1hG0fE9dC8bA7",
                    shortId = "f4e3d2c1",
                    spiderX = "/",
                    pingMs = 125,
                    isFavorite = false
                ),
                VpnServer(
                    name = "SG - Singapore Cyber Trojan",
                    protocol = "Trojan",
                    serverAddress = "104.18.32.7",
                    port = 443,
                    uuid = "CyberPass2026Secure",
                    network = "ws",
                    path = "/cyber-ws",
                    security = "tls",
                    sni = "cloudflare.com",
                    pingMs = 165,
                    isFavorite = false
                )
            )

            for (server in seeds) {
                db.vpnServerDao().insertServer(server)
            }
        }
    }
}
