package com.nizamisadykhov.notepad.features.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nizamisadykhov.notepad.data.INoteRepository
import com.nizamisadykhov.notepad.data.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val noteRepository: INoteRepository
) : ViewModel() {

    val notes: StateFlow<List<Note>> = noteRepository
        .getNotes()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    suspend fun addNote(note: Note) = noteRepository.addNote(note)

}
