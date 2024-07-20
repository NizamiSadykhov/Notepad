package com.nizamisadykhov.notepad.features.detail

import androidx.lifecycle.SavedStateHandle
import com.nizamisadykhov.notepad.MainDispatcherRule
import com.nizamisadykhov.notepad.data.INoteRepository
import com.nizamisadykhov.notepad.data.Note
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.Date
import java.util.UUID

class NoteDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val id = UUID.randomUUID()
    private val savedStateHandle = SavedStateHandle(mapOf("noteId" to id))
    private val note: Note = Note(id = id, "", Date(), false, "")
    private val noteRepository: INoteRepository = mockk { coEvery { getNote(id) } returns note }

    private val sut = NoteDetailViewModel(savedStateHandle, noteRepository)

    @Test
    fun initialize() {
        sut.initialize()
        assertEquals(note, sut.note.value)
        coVerify { noteRepository.getNote(id) }
    }

    @Test
    fun updateNote() {
        val updatedNote = Note.EMPTY
        sut.updateNote { updatedNote }
        assertEquals(updatedNote, sut.note.value)
    }

}
