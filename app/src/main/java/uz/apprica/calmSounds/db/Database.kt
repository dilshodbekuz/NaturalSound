package uz.apprica.calmSounds.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SoundEntity::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun soundDao(): SoundDao
}