package com.nizamisadykhov.notepad.data

import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface INoteRepository {
    fun getNotes(): Flow<List<Note>>
    suspend fun getNote(id: UUID): Note
    fun updateNote(note: Note)
    suspend fun addNote(note: Note)
}