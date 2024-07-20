package com.nizamisadykhov.notepad.di

import android.content.Context
import androidx.room.Room
import com.nizamisadykhov.notepad.data.INoteRepository
import com.nizamisadykhov.notepad.data.NoteRepository
import com.nizamisadykhov.notepad.data.database.NoteDao
import com.nizamisadykhov.notepad.data.database.NoteDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Singleton
    @Binds
    fun bindNoteRepository(noteRepository: NoteRepository): INoteRepository

    companion object {

        @Singleton
        @Provides
        fun provideNoteDao(
            @ApplicationContext context: Context
        ): NoteDao {
            val noteDatabase = Room.databaseBuilder(
                context = context,
                klass = NoteDatabase::class.java,
                name = NoteDatabase.DATABASE_NAME
            )
                .build()

            return noteDatabase.noteDao()
        }

        @Singleton
        @Provides
        fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob())
    }
}