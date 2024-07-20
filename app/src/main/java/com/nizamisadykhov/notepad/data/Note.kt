package com.nizamisadykhov.notepad.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Note(
    @PrimaryKey val id: UUID,
    val title: String,
    val date: Date,
    val isSolved: Boolean,
    val author: String,
    val photoFileName: String? = null
) {
    companion object {

        val EMPTY: Note
            get() = Note(
                id = UUID.randomUUID(),
                title = "",
                date = Date(),
                isSolved = false,
                author = ""
            )

    }
}
