package com.example.notesapproom

import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notesapproom.adapters.NotesAdapter
import com.example.notesapproom.database.Note
import com.example.notesapproom.database.NoteDatabase
import com.example.notesapproom.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val notesDao by lazy { NoteDatabase.getDatabase(this).noteDao() }
    private lateinit var binding: ActivityMainBinding

    private lateinit var recyclerAdapter: NotesAdapter
    private lateinit var allNotes: List<Note>


    /**
     * This project makes Room work without a View Model, the Recycler View is only updated
     * each time the screen is rotated. The solutions I tried to update the RV immediately didn't work,
     * but calling fetchNote on add/update/delete functionalities worked
     * View Models and Live Data allow us to update the RV each time we make a change to our database.
     */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        allNotes = listOf()

        binding.floatingActionButton.setOnClickListener { showAddNoteDialog() }

        fetchNotes()

        updateRV()

    }

    private fun updateRV() {
        recyclerAdapter = NotesAdapter(allNotes, this)
        //recyclerAdapter = NotesAdapter(fetchNotes(), this)
        binding.notesRV.adapter = recyclerAdapter
        binding.notesRV.layoutManager = GridLayoutManager(this, 2)
    }

    private fun fetchNotes() /*: List<Note>*/{
        CoroutineScope(IO).launch {
            val data = async {
                notesDao.getNotes()
            }.await()
            if (data.isNotEmpty()) {
                allNotes = data
                withContext(Main) { updateRV() }
            } else {
                Log.d("dd", "Couldn't get notes")
            }
        }
       // return allNotes
    }

    private fun editNote(notePK: Int, text: String) {
        CoroutineScope(IO).launch {
            notesDao.updateNote(Note(notePK, text))
        }
        Toast.makeText(this, "Note Updated", Toast.LENGTH_LONG).show()
    }

    private fun deleteNote(notePK: Int) {
        CoroutineScope(IO).launch {
            notesDao.deleteNote(Note(notePK, ""))
        }
        Toast.makeText(applicationContext, "Note Deleted", Toast.LENGTH_LONG).show()
    }


    private fun showAddNoteDialog() {
        val dialog = Dialog(this, R.style.Theme_AppCompat_DayNight)
        dialog.setContentView(R.layout.add_note_dialog)
        dialog.setCanceledOnTouchOutside(true)

        val addBtnD = dialog.findViewById<Button>(R.id.addBtnD)
        val addNoteET = dialog.findViewById<EditText>(R.id.addNoteET)

        addBtnD.setOnClickListener {
            if (addNoteET.text.isNotEmpty()) {
                //////// NOTE: when I click space at the end of the note, it saves an empty note. Try to solve it/////////

                CoroutineScope(IO).launch {
                    notesDao.insertNote(Note(0, addNoteET.text.toString()))
                }
                addNoteET.text.clear()
                fetchNotes()
                dialog.dismiss()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Type something to add a note",
                    Toast.LENGTH_LONG
                ).show()
                // this Toast is not showing
            }
        }
        dialog.show()
    }


    fun showEditDeleteDialog(id: Int, noteText: String) {
        val dialog = Dialog(
            this,
            R.style.Theme_AppCompat_DayNight) //The second argument is to make the dialog in full screen

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.edit_delete_dialog)

        val deleteBtn = dialog.findViewById<Button>(R.id.deleteBtn)
        val editBtn = dialog.findViewById<Button>(R.id.editBtn)
        val editDeleteET = dialog.findViewById<EditText>(R.id.editDeleteET)
        editDeleteET.setText(noteText)

        editBtn.setOnClickListener {
            editNote(id, editDeleteET.text.toString())
            fetchNotes() // To update the RV with the new changes
            dialog.dismiss()
        }
        deleteBtn.setOnClickListener {
            displayDeleteConformationDialog(id)
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun displayDeleteConformationDialog(id: Int) {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                deleteNote(id)
                fetchNotes() // To update the RV with the new changes
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
            })
        val alert = dialogBuilder.create()
        alert.setTitle("Delete Confirmation")
        alert.show()
    }

}