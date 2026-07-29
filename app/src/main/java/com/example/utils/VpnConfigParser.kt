package com.example.utils

import android.net.Uri
import android.util.Base64
import com.example.data.VpnServer
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder

object VpnConfigParser {

    fun parseShareLink(rawLink: String): VpnServer? {
        val link = rawLink.trim()
        return when {
            link.startsWith("vless://", ignoreCase = true) -> parseVless(link)
            link.startsWith("vmess://", ignoreCase = true) -> parseVmess(link)
            link.startsWith("trojan://", ignoreCase = true) -> parseTrojan(link)
            link.startsWith("ss://", ignoreCase = true) -> parseShadowsocks(link)
            else -> null
        }
    }

    private fun parseVless(link: String): VpnServer? {
        return try {
            val withoutPrefix = link.substring("vless://".length)
            val fragmentSplit = withoutPrefix.split("#", limit = 2)
            val mainPart = fragmentSplit[0]
            val serverName = if (fragmentSplit.size > 1) {
                URLDecoder.decode(fragmentSplit[1], "UTF-8")
            } else {
                "VLESS Reality Node"
            }

            val atSplit = mainPart.split("@", limit = 2)
            if (atSplit.size < 2) return null
            val uuid = atSplit[0]
            val rest = atSplit[1]

            val querySplit = rest.split("?", limit = 2)
            val hostPort = querySplit[0]
            val queryStr = if (querySplit.size > 1) querySplit[1] else ""

            val hostPortSplit = hostPort.split(":", limit = 2)
            val serverAddress = hostPortSplit[0]
            val port = hostPortSplit.getOrNull(1)?.toIntOrNull() ?: 443

            val queryParams = parseQueryParams(queryStr)

            VpnServer(
                name = serverName,
                protocol = "VLESS",
                serverAddress = serverAddress,
                port = port,
                uuid = uuid,
                flow = queryParams["flow"] ?: "xtls-rprx-vision",
                encryption = queryParams["encryption"] ?: "none",
                network = queryParams["type"] ?: "tcp",
                headerType = queryParams["headerType"] ?: "none",
                host = queryParams["host"] ?: "",
                path = queryParams["path"] ?: "",
                security = queryParams["security"] ?: "reality",
                sni = queryParams["sni"] ?: "discord.com",
                fingerprint = queryParams["fp"] ?: "chrome",
                publicKey = queryParams["pbk"] ?: "",
                shortId = queryParams["sid"] ?: "",
                spiderX = queryParams["spx"] ?: "/"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseVmess(link: String): VpnServer? {
        return try {
            val encoded = link.substring("vmess://".length)
            val decodedJson = String(Base64.decode(encoded, Base64.DEFAULT or Base64.NO_WRAP or Base64.URL_SAFE), Charsets.UTF_8)
            val json = JSONObject(decodedJson)

            VpnServer(
                name = json.optString("ps", "VMess Node"),
                protocol = "VMess",
                serverAddress = json.optString("add", "127.0.0.1"),
                port = json.optString("port", "443").toIntOrNull() ?: 443,
                uuid = json.optString("id", ""),
                network = json.optString("net", "tcp"),
                security = json.optString("tls", "none"),
                sni = json.optString("sni", json.optString("host", "")),
                host = json.optString("host", ""),
                path = json.optString("path", "")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseTrojan(link: String): VpnServer? {
        return try {
            val withoutPrefix = link.substring("trojan://".length)
            val fragmentSplit = withoutPrefix.split("#", limit = 2)
            val mainPart = fragmentSplit[0]
            val serverName = if (fragmentSplit.size > 1) {
                URLDecoder.decode(fragmentSplit[1], "UTF-8")
            } else {
                "Trojan Node"
            }

            val atSplit = mainPart.split("@", limit = 2)
            if (atSplit.size < 2) return null
            val password = atSplit[0]
            val rest = atSplit[1]

            val querySplit = rest.split("?", limit = 2)
            val hostPort = querySplit[0]
            val queryStr = if (querySplit.size > 1) querySplit[1] else ""

            val hostPortSplit = hostPort.split(":", limit = 2)
            val serverAddress = hostPortSplit[0]
            val port = hostPortSplit.getOrNull(1)?.toIntOrNull() ?: 443

            val queryParams = parseQueryParams(queryStr)

            VpnServer(
                name = serverName,
                protocol = "Trojan",
                serverAddress = serverAddress,
                port = port,
                uuid = password,
                network = queryParams["type"] ?: "tcp",
                security = queryParams["security"] ?: "tls",
                sni = queryParams["sni"] ?: serverAddress
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseShadowsocks(link: String): VpnServer? {
        return try {
            val withoutPrefix = link.substring("ss://".length)
            val fragmentSplit = withoutPrefix.split("#", limit = 2)
            val mainPart = fragmentSplit[0]
            val serverName = if (fragmentSplit.size > 1) {
                URLDecoder.decode(fragmentSplit[1], "UTF-8")
            } else {
                "Shadowsocks Node"
            }

            val atSplit = mainPart.split("@", limit = 2)
            var userPass = ""
            var hostPort = ""
            if (atSplit.size == 2) {
                val encodedUserPass = atSplit[0]
                userPass = try {
                    String(Base64.decode(encodedUserPass, Base64.DEFAULT), Charsets.UTF_8)
                } catch (e: Exception) {
                    encodedUserPass
                }
                hostPort = atSplit[1]
            } else {
                hostPort = mainPart
            }

            val hostPortSplit = hostPort.split(":", limit = 2)
            val serverAddress = hostPortSplit[0]
            val port = hostPortSplit.getOrNull(1)?.toIntOrNull() ?: 8388

            VpnServer(
                name = serverName,
                protocol = "Shadowsocks",
                serverAddress = serverAddress,
                port = port,
                uuid = userPass,
                network = "tcp",
                security = "none"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseQueryParams(queryStr: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        if (queryStr.isEmpty()) return map
        val pairs = queryStr.split("&")
        for (pair in pairs) {
            val kv = pair.split("=", limit = 2)
            if (kv.isNotEmpty()) {
                val key = kv[0]
                val value = if (kv.size > 1) URLDecoder.decode(kv[1], "UTF-8") else ""
                map[key] = value
            }
        }
        return map
    }

    fun generateShareLink(server: VpnServer): String {
        return when (server.protocol.uppercase()) {
            "VLESS" -> {
                val params = mutableListOf<String>()
                params.add("type=${server.network}")
                params.add("security=${server.security}")
                if (server.flow.isNotEmpty()) params.add("flow=${server.flow}")
                if (server.sni.isNotEmpty()) params.add("sni=${server.sni}")
                if (server.fingerprint.isNotEmpty()) params.add("fp=${server.fingerprint}")
                if (server.publicKey.isNotEmpty()) params.add("pbk=${server.publicKey}")
                if (server.shortId.isNotEmpty()) params.add("sid=${server.shortId}")
                if (server.spiderX.isNotEmpty() && server.spiderX != "/") params.add("spx=${server.spiderX}")
                if (server.path.isNotEmpty()) params.add("path=${URLEncoder.encode(server.path, "UTF-8")}")

                val query = params.joinToString("&")
                val encodedName = URLEncoder.encode(server.name, "UTF-8")
                "vless://${server.uuid}@${server.serverAddress}:${server.port}?$query#$encodedName"
            }
            "TROJAN" -> {
                val encodedName = URLEncoder.encode(server.name, "UTF-8")
                "trojan://${server.uuid}@${server.serverAddress}:${server.port}?security=${server.security}&sni=${server.sni}#$encodedName"
            }
            else -> {
                val encodedName = URLEncoder.encode(server.name, "UTF-8")
                "${server.protocol.lowercase()}://${server.uuid}@${server.serverAddress}:${server.port}#$encodedName"
            }
        }
    }
}
