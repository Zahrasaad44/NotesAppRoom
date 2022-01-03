package com.example.notesapproom.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapproom.MainActivity
import com.example.notesapproom.database.Note
import com.example.notesapproom.databinding.NoteRowBinding

class NotesAdapter(private var notes: List<Note>, private val activity: MainActivity): RecyclerView.Adapter<NotesAdapter.NotesViewHolder>(){
    class NotesViewHolder(val binding: NoteRowBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(NoteRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = notes[position]

        holder.binding.apply {
            noteTV.text = note.noteText
            noteCV.setOnClickListener { activity.showEditDeleteDialog(note.pk,
                noteTV.text as String
            ) }
        }
    }

    override fun getItemCount() = notes.size

//    fun updateNotes(userNotes: List<Note>) {
//        this.notes = userNotes
//        notifyDataSetChanged()
//    }

}
