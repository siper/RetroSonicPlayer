package ru.stersh.apisonic.room.serversettings

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerSettingsDao {

    @Query("SELECT * FROM server_settings WHERE is_active = 1")
    fun flowActive(): Flow<ServerSettingsEntity?>

    @Query("SELECT COUNT(*) FROM server_settings WHERE id != :id")
    suspend fun getCountExcept(id: Long): Int

    @Query("SELECT COUNT(*) FROM server_settings")
    suspend fun getCount(): Int

    @Query("SELECT * FROM server_settings WHERE is_active = 1")
    suspend fun getActive(): ServerSettingsEntity?

    @Query("SELECT * FROM server_settings WHERE id = :settingsId")
    suspend fun getSettings(settingsId: Long): ServerSettingsEntity?

    @Query("UPDATE server_settings SET is_active = 1 WHERE id = :serverSettingsId")
    suspend fun activate(serverSettingsId: Long)

    @Query("UPDATE server_settings SET is_active = 0")
    suspend fun deactivateAll()

    @Query("DELETE FROM server_settings WHERE id = :id")
    suspend fun remove(id: Long)

    @Query("SELECT * FROM server_settings")
    fun flowAll(): Flow<List<ServerSettingsEntity>>

    @Query("SELECT * FROM server_settings")
    suspend fun getAll(): List<ServerSettingsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(serverSettings: ServerSettingsEntity): Long

    @Delete
    suspend fun delete(serverSettings: ServerSettingsEntity)

    @Transaction
    suspend fun delete(id: Long) {
        remove(id)
        val all = getAll()
        val active = all.firstOrNull { it.isActive }
        if (active == null) {
            val first = all.firstOrNull() ?: return
            setActive(first)
        }
    }

    @Transaction
    suspend fun setActive(serverSettingsId: Long) {
        deactivateAll()
        activate(serverSettingsId)
    }

    suspend fun setActive(serverSettings: ServerSettingsEntity) {
        setActive(serverSettings.id)
    }
}
