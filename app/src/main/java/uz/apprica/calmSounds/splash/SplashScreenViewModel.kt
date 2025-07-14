package uz.apprica.calmSounds.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.apprica.calmSounds.db.SoundDao
import uz.apprica.calmSounds.db.SoundEntity
import uz.apprica.calmSounds.home.image
import uz.apprica.calmSounds.home.name
import uz.apprica.calmSounds.home.sound
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val soundDao: SoundDao
) : ViewModel() {


    private val firebase = FirebaseFirestore.getInstance()

    init {
        setSounds()
    }

    private fun setSounds() {
        firebase.collection("sounds")
            .get()
            .addOnSuccessListener { result ->
                result.forEach { document ->
                    addUser(
                        SoundEntity(
                            id = document.get("id").toString(),
                            name = document.getString(name) ?: "",
                            sound = document.getString(sound) ?: "",
                            image = document.getString(image) ?: ""
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }

    private fun addUser(user: SoundEntity) {
        viewModelScope.launch {
            soundDao.insertSound(user)
        }
    }
}