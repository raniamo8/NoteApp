package com.example.noteapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteapp.MainActivity
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentAddNoteBinding
import com.example.noteapp.databinding.FragmentEditNoteBinding
import com.example.noteapp.model.Note
import com.example.noteapp.viewmodel.NoteViewModel


class EditNoteFragment : Fragment(R.layout.fragment_add_note), MenuProvider {
    private var editNoteBinding: FragmentEditNoteBinding? = null
    private val binding get() = editNoteBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var currentNote: Note

    private val args : EditNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        editNoteBinding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        val menuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel= (activity as MainActivity).noteViewModel
        currentNote = args.note!!

        binding.editNoteTitle.setText(currentNote.noteTitle)
        binding.editNoteDesc.setText(currentNote.noteDescription)
        binding.editNoteFab.setOnClickListener{
            val noteTitle = binding.editNoteTitle.text.toString().trim()
            val noteDescription = binding.editNoteDesc.text.toString().trim()

            if (noteTitle.isNotEmpty()){
                val note = Note(currentNote.noteId, noteTitle, noteDescription)
                notesViewModel.updateNote(note)
                view.findNavController().popBackStack(R.id.homeFragment, false)
            }else{
                Toast.makeText(context, "Bitte den Titel ausfüllen!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteNote(){
        AlertDialog.Builder(activity).apply {
            setTitle("Notiz löschen")
            setMessage("Möchten Sie das Notiz löschen?")
            setPositiveButton("Löschen"){_,_ ->
                notesViewModel.deleteNote((currentNote))
                Toast.makeText(context, "Notiz gelöscht!", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("Abbrechen", null)
        }.create().show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edite_note, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.deleteMenu -> {
                deleteNote()
                true
            }else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        editNoteBinding = null
    }
}