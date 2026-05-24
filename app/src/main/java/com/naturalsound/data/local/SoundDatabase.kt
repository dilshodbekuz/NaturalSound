package com.naturalsound.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "sound_local")
data class SoundLocalEntity(
    @PrimaryKey val id: String,
    val isFavorite: Boolean = false,
    val localPath: String? = null
)

@Dao
interface SoundDao {
    @Query("SELECT * FROM sound_local")
    fun getLocalData(): Flow<List<SoundLocalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SoundLocalEntity)

    @Query("UPDATE sound_local SET isFavorite = :isFav WHERE id = :soundId")
    suspend fun updateFavorite(soundId: String, isFav: Boolean)

    @Query("UPDATE sound_local SET localPath = :path WHERE id = :soundId")
    suspend fun updateLocalPath(soundId: String, path: String)

    // Yo'q bo'lsa yaratib, bor bo'lsa update
    @Transaction
    suspend fun upsertFavorite(soundId: String, isFavorite: Boolean) {
        val existing = getById(soundId)
        if (existing == null) insert(SoundLocalEntity(id = soundId, isFavorite = isFavorite))
        else updateFavorite(soundId, isFavorite)
    }

    @Query("SELECT * FROM sound_local WHERE id = :soundId LIMIT 1")
    suspend fun getById(soundId: String): SoundLocalEntity?
}

@Database(entities = [SoundLocalEntity::class], version = 1, exportSchema = false)
abstract class NaturalSoundDatabase : RoomDatabase() {
    abstract fun soundDao(): SoundDao
}
