package com.nizamisadykhov.notepad.data

import com.nizamisadykhov.notepad.data.database.NoteDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val coroutineScope: CoroutineScope
) : INoteRepository {

    override fun getNotes(): Flow<List<Note>> = noteDao.getNotes()

    override suspend fun getNote(id: UUID): Note = noteDao.getNote(id)

    override fun updateNote(note: Note) {
        coroutineScope.launch { noteDao.updateNote(note) }
    }

    override suspend fun addNote(note: Note) = noteDao.addNote(note)

}