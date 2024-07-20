package com.nizamisadykhov.notepad.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nizamisadykhov.notepad.data.Note
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE id=(:id)")
    suspend fun getNote(id: UUID): Note

    @Update
    suspend fun updateNote(note: Note)

    @Insert
    suspend fun addNote(note: Note)

}
