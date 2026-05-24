package com.naturalsound.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.naturalsound.data.model.SoundDto
import com.naturalsound.data.model.toDomain
import com.naturalsound.domain.model.Sound
import com.naturalsound.domain.model.SoundCategory
import com.naturalsound.domain.repository.SoundRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : SoundRepository {

    private val soundsRef = firebaseDatabase.getReference("sounds")

    override fun getAllSounds(): Flow<List<Sound>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { child ->
                    val id       = child.key ?: return@mapNotNull null
                    val name     = child.child("name").getValue(String::class.java) ?: ""
                    val url      = child.child("url").getValue(String::class.java) ?: ""
                    val category = child.child("category").getValue(String::class.java) ?: "ALL"
                    val emoji    = child.child("emoji").getValue(String::class.java) ?: "🎵"
                    @Suppress("UNCHECKED_CAST")
                    val tags = runCatching {
                        (child.child("tags").value as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                    }.getOrDefault(emptyList())
                    SoundDto(id = id, name = name, url = url, category = category, emoji = emoji, tags = tags)
                }
                Log.d("SoundRepo", "Yuklandi: ${list.size} ta sound")
                trySend(list.map { it.toDomain() })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SoundRepo", "RTDB xato: ${error.message}")
                close(error.toException())
            }
        }
        soundsRef.addValueEventListener(listener)
        awaitClose { soundsRef.removeEventListener(listener) }
    }

    override fun getSoundsByCategory(category: SoundCategory): Flow<List<Sound>> =
        getAllSounds().map { it.filter { s -> s.category == category } }

    override fun searchSounds(query: String): Flow<List<Sound>> =
        getAllSounds().map { sounds ->
            sounds.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.category.label.contains(query, ignoreCase = true)
            }
        }
}
