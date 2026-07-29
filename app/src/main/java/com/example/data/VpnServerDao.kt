package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VpnServerDao {
    @Query("SELECT * FROM vpn_servers ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllServers(): Flow<List<VpnServer>>

    @Query("SELECT * FROM vpn_servers WHERE id = :id")
    suspend fun getServerById(id: Long): VpnServer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: VpnServer): Long

    @Update
    suspend fun updateServer(server: VpnServer)

    @Delete
    suspend fun deleteServer(server: VpnServer)

    @Query("DELETE FROM vpn_servers WHERE id = :id")
    suspend fun deleteServerById(id: Long)

    @Query("UPDATE vpn_servers SET pingMs = :pingMs WHERE id = :id")
    suspend fun updatePing(id: Long, pingMs: Int)

    @Query("UPDATE vpn_servers SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: Long, isFav: Boolean)
}
