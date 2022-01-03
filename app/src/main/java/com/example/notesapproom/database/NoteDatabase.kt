package com.example.notesapproom.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Note::class], version = 1, exportSchema = false) // "exportSchema" set it true to manage versions
abstract class NoteDatabase: RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile  //writes to this field are immediately made visible to other threads
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            val temInstance = INSTANCE
            if (temInstance != null) {
                return temInstance
            }
            // if the instance = null
            synchronized(this) {
                // "synchronized" to make sure that every thing happens in the same thread,
                // protection from concurrent execution on multiple threads
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "Notes"
                ).fallbackToDestructiveMigration() //Destroys(deletes) old com.example.notesapproom.database on version change
                    .build()  // To create new one
                INSTANCE = instance
                return instance
            }
        }
    }

}