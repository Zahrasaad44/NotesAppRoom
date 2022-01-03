package com.example.notesapproom.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val pk: Int,
    val noteText: String)
