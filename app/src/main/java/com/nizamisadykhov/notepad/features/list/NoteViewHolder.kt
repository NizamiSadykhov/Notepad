package com.nizamisadykhov.notepad.features.list

import android.text.format.DateFormat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nizamisadykhov.notepad.data.Note
import com.nizamisadykhov.notepad.databinding.NoteListItemBinding
import java.util.UUID

class NoteViewHolder(
    private val binding: NoteListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(note: Note, onNoteClicked: (noteId: UUID) -> Unit) {
        binding.noteTitle.text = note.title
        binding.noteDate.text = DateFormat.format(DATE_FORMAT, note.date).toString()
        binding.root.setOnClickListener {
            onNoteClicked(note.id)
        }
        binding.noteSolved.isVisible = note.isSolved
    }

    companion object {
        private const val DATE_FORMAT = "dd.MM.yyyy"
    }
}