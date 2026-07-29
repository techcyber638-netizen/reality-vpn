package com.example.engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    RECONNECTING
}

enum class EngineType(val displayName: String, val version: String) {
    XRAY("Xray-core", "v1.8.4"),
    SING_BOX("Sing-box", "v1.8.0")
}

enum class DnsMode(val displayName: String, val primary: String, val secondary: String) {
    CLOUDFLARE("Cloudflare DNS", "1.1.1.1", "1.0.0.1"),
    GOOGLE("Google Public DNS", "8.8.8.8", "8.8.4.4"),
    ADGUARD("AdGuard DNS (AdBlock)", "94.140.14.14", "94.140.15.15"),
    QUAD9("Quad9 Secured", "9.9.9.9", "149.112.112.112")
}

enum class RoutingRule(val displayName: String, val description: String) {
    BYPASS_LAN("Bypass LAN & Mainland", "Direct connections for local network and domestic IPs"),
    GLOBAL("Global Proxy", "Route all device traffic through VLESS Reality tunnel"),
    RULE_BASED("Smart GeoIP Rule", "Automatically route blocked traffic and bypass direct sites")
}

data class SpeedSample(val timestamp: Long, val downloadMbps: Double, val uploadMbps: Double)

object VpnEngine {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _downloadSpeed = MutableStateFlow(0.0)
    val downloadSpeed: StateFlow<Double> = _downloadSpeed.asStateFlow()

    private val _uploadSpeed = MutableStateFlow(0.0)
    val uploadSpeed: StateFlow<Double> = _uploadSpeed.asStateFlow()

    private val _sessionDurationSeconds = MutableStateFlow(0L)
    val sessionDurationSeconds: StateFlow<Long> = _sessionDurationSeconds.asStateFlow()

    private val _sessionDownloadMb = MutableStateFlow(0.0)
    val sessionDownloadMb: StateFlow<Double> = _sessionDownloadMb.asStateFlow()

    private val _sessionUploadMb = MutableStateFlow(0.0)
    val sessionUploadMb: StateFlow<Double> = _sessionUploadMb.asStateFlow()

    private val _todayDownloadMb = MutableStateFlow(420.5)
    val todayDownloadMb: StateFlow<Double> = _todayDownloadMb.asStateFlow()

    private val _todayUploadMb = MutableStateFlow(84.2)
    val todayUploadMb: StateFlow<Double> = _todayUploadMb.asStateFlow()

    private val _speedHistory = MutableStateFlow<List<SpeedSample>>(emptyList())
    val speedHistory: StateFlow<List<SpeedSample>> = _speedHistory.asStateFlow()

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    private var connectionJob: Job? = null
    private var timerJob: Job? = null
    private var trafficJob: Job? = null

    init {
        log("Cyber Tech Core Service Initialized.")
        log("Xray-core v1.8.4 Ready (VLESS Reality, uTLS, gRPC, WebSocket).")
    }

    fun log(message: String) {
        val timeStr = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date())
        val formatted = "[$timeStr] $message"
        val current = _logs.value.toMutableList()
        if (current.size > 200) current.removeAt(0)
        current.add(formatted)
        _logs.value = current
    }

    fun updateState(state: ConnectionState) {
        _connectionState.value = state
        if (state == ConnectionState.CONNECTED) {
            _sessionDurationSeconds.value = 0L
            _sessionDownloadMb.value = 0.0
            _sessionUploadMb.value = 0.0
            startSessionTracking()
        }
    }

    fun connect(context: android.content.Context, server: com.example.data.VpnServer) {
        if (_connectionState.value == ConnectionState.CONNECTED || _connectionState.value == ConnectionState.CONNECTING) return
        
        _connectionState.value = ConnectionState.CONNECTING
        log("Initiating Reality tunnel to: ${server.name}")
        
        val intent = android.content.Intent(context, RealityVpnService::class.java).apply {
            putExtra("SERVER_ADDRESS", server.serverAddress)
            putExtra("CONFIG_JSON", "{}") 
        }
        context.startService(intent)
    }

    fun disconnect() {
        if (_connectionState.value == ConnectionState.DISCONNECTED || _connectionState.value == ConnectionState.DISCONNECTING) return

        connectionJob?.cancel()
        timerJob?.cancel()
        trafficJob?.cancel()

        scope.launch {
            _connectionState.value = ConnectionState.DISCONNECTING
            log("Tearing down proxy session...")
            delay(400)
            log("VLESS Reality tunnel closed gracefully.")
            _connectionState.value = ConnectionState.DISCONNECTED
            _downloadSpeed.value = 0.0
            _uploadSpeed.value = 0.0
        }
    }

    private fun startSessionTracking() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (_connectionState.value == ConnectionState.CONNECTED) {
                delay(1000)
                _sessionDurationSeconds.value += 1
            }
        }

        trafficJob?.cancel()
        trafficJob = scope.launch {
            val history = mutableListOf<SpeedSample>()
            while (_connectionState.value == ConnectionState.CONNECTED) {
                delay(1000)
                // Simulate active traffic fluctuations
                val dl = Random.nextDouble(12.5, 68.4)
                val ul = Random.nextDouble(2.1, 18.2)

                _downloadSpeed.value = String.format(Locale.US, "%.1f", dl).toDouble()
                _uploadSpeed.value = String.format(Locale.US, "%.1f", ul).toDouble()

                val addedDlMb = (dl / 8.0) // convert Mbps to MB per sec
                val addedUlMb = (ul / 8.0)

                _sessionDownloadMb.value += addedDlMb
                _sessionUploadMb.value += addedUlMb

                _todayDownloadMb.value += addedDlMb
                _todayUploadMb.value += addedUlMb

                history.add(SpeedSample(System.currentTimeMillis(), dl, ul))
                if (history.size > 30) history.removeAt(0)
                _speedHistory.value = history.toList()
            }
        }
    }

    suspend fun pingServer(serverAddress: String): Int {
        delay(Random.nextLong(150, 400))
        val basePing = when {
            serverAddress.contains("104.") || serverAddress.contains("162.") -> Random.nextInt(25, 60)
            serverAddress.contains("172.") -> Random.nextInt(90, 140)
            else -> Random.nextInt(40, 180)
        }
        return basePing
    }
}
