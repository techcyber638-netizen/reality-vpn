package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vpn_servers")
data class VpnServer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val protocol: String, // "VLESS", "VMess", "Trojan", "Shadowsocks"
    val serverAddress: String,
    val port: Int,
    val uuid: String, // User ID or Password
    val flow: String = "xtls-rprx-vision", // e.g. "xtls-rprx-vision" or ""
    val encryption: String = "none",
    val network: String = "tcp", // "tcp", "ws", "grpc", "httpupgrade", "quic"
    val headerType: String = "none",
    val host: String = "",
    val path: String = "",
    val security: String = "reality", // "reality", "tls", "none"
    val sni: String = "discord.com",
    val fingerprint: String = "chrome", // "chrome", "firefox", "safari", "random"
    val publicKey: String = "", // Reality pbk
    val shortId: String = "", // Reality sid
    val spiderX: String = "/", // Reality spx
    val pingMs: Int = -1, // -1 means untested
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
