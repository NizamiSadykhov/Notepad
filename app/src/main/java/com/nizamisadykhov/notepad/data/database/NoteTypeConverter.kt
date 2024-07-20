package com.nizamisadykhov.notepad.data.database

import androidx.room.TypeConverter
import java.util.Date

class NoteTypeConverter {

    @TypeConverter
    fun fromDate(date: Date): Long = date.time

    @TypeConverter
    fun toDate(milliSinceEpoch: Long) = Date(milliSinceEpoch)
}