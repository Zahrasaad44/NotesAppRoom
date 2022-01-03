package com.example.notesapproom.database

import androidx.room.*
import com.example.notesapproom.database.Note


@Dao  // Data Access Objects
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) //To ignore the new record that's identical to an existing one
    suspend fun insertNote(note: Note)

    @Query("SELECT * FROM Notes ORDER BY pk ASC")
    fun getNotes(): List<Note>

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}