package com.notes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes")
    fun getAll(): List<NoteDbo>

    @Insert
    fun insertAll(vararg notes: NoteDbo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNote(note: NoteDbo)

    @Query("DELETE FROM notes WHERE id = :id")
    fun deleteNoteByID(id: Long)

}