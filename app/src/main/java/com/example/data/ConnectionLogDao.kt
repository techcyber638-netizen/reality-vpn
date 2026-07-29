package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class UsageStats(
    val totalDownloadMb: Double,
    val totalUploadMb: Double,
    val totalDurationSec: Long
)

@Dao
interface ConnectionLogDao {
    @Query("SELECT * FROM connection_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<ConnectionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ConnectionLog)

    @Query("SELECT COALESCE(SUM(downloadMb), 0.0) AS totalDownloadMb, COALESCE(SUM(uploadMb), 0.0) AS totalUploadMb, COALESCE(SUM(durationSeconds), 0) AS totalDurationSec FROM connection_logs")
    fun getTotalStats(): Flow<UsageStats>

    @Query("DELETE FROM connection_logs")
    suspend fun clearLogs()
}
