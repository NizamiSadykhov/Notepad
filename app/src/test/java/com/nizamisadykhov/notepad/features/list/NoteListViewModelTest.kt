package com.nizamisadykhov.notepad.features.list

import com.nizamisadykhov.notepad.MainDispatcherRule
import com.nizamisadykhov.notepad.data.INoteRepository
import com.nizamisadykhov.notepad.data.Note
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NoteListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val noteRepository: INoteRepository = mockk(relaxed = true)

    private val sut by lazy {
        NoteListViewModel(noteRepository)
    }

    @Test
    fun getNotes() = runTest {
        val notesStream = MutableSharedFlow<List<Note>>()
        every { noteRepository.getNotes() } returns notesStream

        assertTrue(sut.notes.value.isEmpty())


        val firstNotes = listOf(Note.EMPTY)
        notesStream.emit(firstNotes)
        assertEquals(firstNotes, sut.notes.value)

        val secondNotes = listOf(Note.EMPTY, Note.EMPTY)
        notesStream.emit(secondNotes)
        assertEquals(secondNotes, sut.notes.value)

        verify { noteRepository.getNotes() }
    }

    @Test
    fun addNote() = runTest {
        val note = Note.EMPTY
        sut.addNote(note)
        coVerify { noteRepository.updateNote(note) }
    }
}