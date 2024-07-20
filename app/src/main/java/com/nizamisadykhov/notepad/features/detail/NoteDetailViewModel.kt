package com.nizamisadykhov.notepad.features.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nizamisadykhov.notepad.data.INoteRepository
import com.nizamisadykhov.notepad.data.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val noteRepository: INoteRepository,
) : ViewModel() {

    private val _note: MutableStateFlow<Note> = MutableStateFlow(Note.EMPTY)
    val note: StateFlow<Note> = _note.asStateFlow()

    fun initialize() {
        viewModelScope.launch {
            val noteId = NoteDetailFragmentArgs
                .fromSavedStateHandle(savedStateHandle)
                .noteId
            _note.value = noteRepository.getNote(noteId)
        }
    }

    fun updateNote(onUpdate: (Note) -> Note) = _note.update(onUpdate)

    override fun onCleared() {
        super.onCleared()
        noteRepository.updateNote(note.value)
    }
}
