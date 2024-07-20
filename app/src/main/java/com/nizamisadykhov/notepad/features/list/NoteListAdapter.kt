package com.nizamisadykhov.notepad.features.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nizamisadykhov.notepad.data.Note
import com.nizamisadykhov.notepad.databinding.NoteListItemBinding
import java.util.UUID

class NoteListAdapter(
    private val notes: List<Note>,
    private val onNoteClicked: (noteId: UUID) -> Unit
) : RecyclerView.Adapter<NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NoteListItemBinding.inflate(inflater, parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note, onNoteClicked)
    }

    override fun getItemCount(): Int = notes.size

}
