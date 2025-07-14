package uz.apprica.calmSounds.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sounds")
data class SoundEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val name: String,
    val image: String,
    val sound: String,
)