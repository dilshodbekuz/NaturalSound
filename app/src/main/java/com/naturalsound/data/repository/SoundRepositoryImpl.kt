package com.naturalsound.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
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
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth
) : SoundRepository {

    private val soundsRef = firebaseDatabase.getReference("sounds")

    override fun getAllSounds(): Flow<List<Sound>> = callbackFlow {
        if (firebaseAuth.currentUser == null) {
            runCatching { firebaseAuth.signInAnonymously().await() }
                .onFailure { Log.e("SoundRepo", "Anonymous auth xato: ${it.message}") }
        }
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
                Log.e("SoundRepo", "RTDB xato: code=${error.code} ${error.message}")
                close(Exception(error.toFriendlyMessage()))
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

    private fun DatabaseError.toFriendlyMessage(): String = when (code) {
        DatabaseError.PERMISSION_DENIED ->
            "Maʼlumotlarga ruxsat yoʻq. Iltimos keyinroq qayta urinib koʻring."
        DatabaseError.NETWORK_ERROR, DatabaseError.DISCONNECTED ->
            "Internet aloqasi yoʻq. Ulanishni tekshiring."
        DatabaseError.UNAVAILABLE ->
            "Server vaqtincha ishlamayapti. Birozdan soʻng urinib koʻring."
        else ->
            "Ovozlarni yuklab boʻlmadi. Keyinroq qayta urinib koʻring."
    }
}
