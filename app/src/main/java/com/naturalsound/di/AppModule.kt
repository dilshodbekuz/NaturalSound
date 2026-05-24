package com.naturalsound.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.database.FirebaseDatabase
import com.naturalsound.data.local.NaturalSoundDatabase
import com.naturalsound.data.local.SoundDao
import com.naturalsound.data.repository.SoundRepositoryImpl
import com.naturalsound.domain.repository.SoundRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): NaturalSoundDatabase =
        Room.databaseBuilder(ctx, NaturalSoundDatabase::class.java, "naturalsound.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideSoundDao(db: NaturalSoundDatabase): SoundDao = db.soundDao()

    @Provides @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase =
        FirebaseDatabase.getInstance("https://naturalsound-df0da-default-rtdb.firebaseio.com")
            .also { it.setPersistenceEnabled(true) }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindSoundRepository(impl: SoundRepositoryImpl): SoundRepository
}
