package com.nizamisadykhov.notepad.features.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nizamisadykhov.notepad.R
import com.nizamisadykhov.notepad.data.Note
import com.nizamisadykhov.notepad.databinding.FragmentNoteListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

@AndroidEntryPoint
class NoteListFragment : Fragment() {

    private var _binding: FragmentNoteListBinding? = null

    private val binding: FragmentNoteListBinding
        get() = _binding
            ?: throw IllegalStateException("Cannot access binding because it is null. Is the view visible?")

    private val viewModel: NoteListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initMenu()
        _binding = FragmentNoteListBinding.inflate(layoutInflater, container, false)
        binding.noteRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notes.collect { notes ->
                    binding.noteRecyclerView.adapter = NoteListAdapter(notes) { noteId ->
                        val action = NoteListFragmentDirections.showNoteDetail(noteId)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun initMenu() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_note_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.new_note -> {
                        showNewNote()
                        true
                    }

                    else -> false
                }
            }

        }
        activity?.addMenuProvider(menuProvider, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showNewNote() {
        viewLifecycleOwner.lifecycleScope.launch {
            val note = Note(
                id = UUID.randomUUID(),
                title = "",
                date = Date(),
                isSolved = false,
                author = ""
            )
            viewModel.addNote(note)
            findNavController().navigate(
                NoteListFragmentDirections.showNoteDetail(note.id)
            )
        }
    }
}