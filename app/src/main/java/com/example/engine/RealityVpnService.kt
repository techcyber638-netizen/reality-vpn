package com.example.engine

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RealityVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == "STOP") {
            stopVpn()
            return START_NOT_STICKY
        }

        val serverAddress = intent?.getStringExtra("SERVER_ADDRESS") ?: ""
        val configJson = intent?.getStringExtra("CONFIG_JSON") ?: ""

        startVpn(serverAddress, configJson)
        return START_STICKY
    }

    private fun startVpn(serverAddress: String, configJson: String) {
        stopVpn()
        job = scope.launch {
            try {
                // Establish VPN Interface
                val builder = Builder()
                    .setSession("RealityVPN")
                    .setMtu(1500)
                    .addAddress("172.19.0.1", 30)
                    .addDnsServer("8.8.8.8")
                    .addRoute("0.0.0.0", 0)
                
                vpnInterface = builder.establish()
                Log.d("RealityVpnService", "VPN Interface established")

                // In a real implementation, you would start the Sing-box/Xray binary here
                // pointing to the configJson and using the vpnInterface file descriptor.
                // For this project, we are setting up the structure.
                
                VpnEngine.updateState(ConnectionState.CONNECTED)
            } catch (e: Exception) {
                Log.e("RealityVpnService", "Error starting VPN", e)
                VpnEngine.updateState(ConnectionState.DISCONNECTED)
            }
        }
    }

    private fun stopVpn() {
        job?.cancel()
        vpnInterface?.close()
        vpnInterface = null
        VpnEngine.updateState(ConnectionState.DISCONNECTED)
        stopSelf()
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }
}
