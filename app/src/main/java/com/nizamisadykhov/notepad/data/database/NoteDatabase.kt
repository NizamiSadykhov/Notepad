package com.nizamisadykhov.notepad.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nizamisadykhov.notepad.data.Note

@Database(entities = [Note::class], version = 3, exportSchema = false)
@TypeConverters(NoteTypeConverter::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        const val DATABASE_NAME = "note-database"
    }
}
