package com.nizamisadykhov.notepad.data

import com.nizamisadykhov.notepad.data.database.NoteDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID


class NoteRepositoryTest {

    private val noteDao: NoteDao = mockk(relaxed = true)

    @Test
    fun getNotes() = runTest {
        val notesStream: Flow<List<Note>> = MutableSharedFlow()
        every { noteDao.getNotes() } returns notesStream
        val sut = NoteRepository(noteDao, this)
        val actual = sut.getNotes()
        assertEquals(notesStream, actual)
        verify { noteDao.getNotes() }
    }

    @Test
    fun getNote() = runTest {
        val id = UUID.randomUUID()
        val note = Note.EMPTY
        coEvery { noteDao.getNote(id) } returns note
        val sut = NoteRepository(noteDao, this)
        val actual = sut.getNote(id)
        assertEquals(note, actual)
        coVerify { noteDao.getNote(id) }
    }

    @Test
    fun updateNote() = runTest {
        val note = Note.EMPTY
        val sut = NoteRepository(noteDao, this)
        sut.updateNote(note)
        coVerify { noteDao.updateNote(note) }
    }

    @Test
    fun addNote() = runTest {
        val note = Note.EMPTY
        val sut = NoteRepository(noteDao, this)
        sut.addNote(note)
        coVerify { noteDao.addNote(note) }
    }
}