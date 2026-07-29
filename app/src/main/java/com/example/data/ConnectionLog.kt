package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "connection_logs")
data class ConnectionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val serverName: String,
    val protocol: String,
    val durationSeconds: Long,
    val downloadMb: Double,
    val uploadMb: Double
)
