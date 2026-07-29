package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.engine.*
import com.example.utils.VpnConfigParser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VpnViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VpnRepository

    val servers: StateFlow<List<VpnServer>>
    val recentLogs: StateFlow<List<ConnectionLog>>
    val totalStats: StateFlow<UsageStats?>

    private val _selectedServerId = MutableStateFlow<Long?>(null)

    val selectedServer: StateFlow<VpnServer?>

    val connectionState: StateFlow<ConnectionState> = VpnEngine.connectionState
    val downloadSpeed: StateFlow<Double> = VpnEngine.downloadSpeed
    val uploadSpeed: StateFlow<Double> = VpnEngine.uploadSpeed
    val sessionDurationSeconds: StateFlow<Long> = VpnEngine.sessionDurationSeconds
    val sessionDownloadMb: StateFlow<Double> = VpnEngine.sessionDownloadMb
    val sessionUploadMb: StateFlow<Double> = VpnEngine.sessionUploadMb
    val todayDownloadMb: StateFlow<Double> = VpnEngine.todayDownloadMb
    val todayUploadMb: StateFlow<Double> = VpnEngine.todayUploadMb
    val speedHistory: StateFlow<List<SpeedSample>> = VpnEngine.speedHistory
    val engineLogs: StateFlow<List<String>> = VpnEngine.logs

    val engineType = MutableStateFlow(EngineType.XRAY)
    val dnsMode = MutableStateFlow(DnsMode.CLOUDFLARE)
    val routingRule = MutableStateFlow(RoutingRule.BYPASS_LAN)
    val autoReconnect = MutableStateFlow(true)
    val killSwitch = MutableStateFlow(false)
    val isPingingAll = MutableStateFlow(false)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = VpnRepository(db)

        servers = repository.allServers.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        recentLogs = repository.recentLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        totalStats = repository.totalStats.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        selectedServer = combine(servers, _selectedServerId) { serverList, id ->
            if (id != null) {
                serverList.find { it.id == id } ?: serverList.firstOrNull()
            } else {
                serverList.firstOrNull()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        viewModelScope.launch {
            repository.seedInitialServersIfEmpty()
        }
    }

    fun selectServer(server: VpnServer) {
        _selectedServerId.value = server.id
        VpnEngine.log("Selected target server: ${server.name} [${server.protocol} / ${server.serverAddress}]")
    }

    fun toggleConnect(context: android.content.Context) {
        val current = connectionState.value
        val server = selectedServer.value ?: return

        if (current == ConnectionState.CONNECTED || current == ConnectionState.CONNECTING) {
            val duration = sessionDurationSeconds.value
            val dlMb = sessionDownloadMb.value
            val ulMb = sessionUploadMb.value

            VpnEngine.disconnect()

            if (duration > 2) {
                viewModelScope.launch {
                    repository.insertConnectionLog(
                        ConnectionLog(
                            serverName = server.name,
                            protocol = server.protocol,
                            durationSeconds = duration,
                            downloadMb = dlMb,
                            uploadMb = ulMb
                        )
                    )
                }
            }
        } else {
            VpnEngine.connect(
                context = context,
                server = server
            )
        }
    }

    fun addServer(server: VpnServer) {
        viewModelScope.launch {
            val newId = repository.insertServer(server)
            _selectedServerId.value = newId
            VpnEngine.log("Added new proxy config: ${server.name}")
        }
    }

    fun importFromLink(rawLink: String): Boolean {
        val parsed = VpnConfigParser.parseShareLink(rawLink)
        return if (parsed != null) {
            addServer(parsed)
            true
        } else {
            false
        }
    }

    fun deleteServer(server: VpnServer) {
        viewModelScope.launch {
            repository.deleteServer(server)
            VpnEngine.log("Deleted proxy configuration: ${server.name}")
        }
    }

    fun toggleFavorite(server: VpnServer) {
        viewModelScope.launch {
            repository.toggleFavorite(server.id, server.isFavorite)
        }
    }

    fun pingServer(server: VpnServer) {
        viewModelScope.launch {
            VpnEngine.log("Pinging ${server.name} (${server.serverAddress}:${server.port})...")
            val ping = VpnEngine.pingServer(server.serverAddress)
            repository.updatePing(server.id, ping)
            VpnEngine.log("Ping response for ${server.name}: ${ping}ms")
        }
    }

    fun pingAllServers() {
        val currentList = servers.value
        if (currentList.isEmpty() || isPingingAll.value) return

        viewModelScope.launch {
            isPingingAll.value = true
            VpnEngine.log("Testing latency for all saved nodes...")
            for (server in currentList) {
                val ping = VpnEngine.pingServer(server.serverAddress)
                repository.updatePing(server.id, ping)
            }
            isPingingAll.value = false
            VpnEngine.log("Completed latency test for all nodes.")
        }
    }
}
