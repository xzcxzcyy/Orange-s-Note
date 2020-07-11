package cloud.banson.orangeNote.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cloud.banson.orangeNote.R
import cloud.banson.orangeNote.database.Note
import cloud.banson.orangeNote.database.NoteDatabase
import cloud.banson.orangeNote.databinding.FragmentListBinding

class ListFragment : Fragment() {
    companion object {
        private const val sortByTitle = 0
        private const val sortByTitleDescend = 1
        private const val sortByTime = 2
        private const val sortByTimeDescend = 3
    }

    private var noteList: List<Note>? = null
    private var sortOption = sortByTimeDescend

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding: FragmentListBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list, container, false
        )

        val application = requireNotNull(this.activity).application

        val dataSource = NoteDatabase.getInstance(application).noteDatabaseDao

        val viewModelFactory = ListViewModelFactory(dataSource, application)

        val viewModel = ViewModelProvider(this, viewModelFactory).get(ListViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.navigateToDetailsFragment.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                this.findNavController().navigate(
                    ListFragmentDirections
                        .actionListFragmentToDetailsFragment(it)
                )
                viewModel.doneNavigating()
            }
        })

        val adapter = NoteAdapter(NoteListener { id: Long ->
            viewModel.onItemClicked(id)
        })
        binding.noteList.adapter = adapter

        viewModel.noteBook.observe(viewLifecycleOwner, Observer { newList ->
            noteList = newList
            updateNow(noteList, adapter)
        })

        return binding.root
    }

    private fun updateNow(newList: List<Note>?, adapter: NoteAdapter) {
        newList?.let { thisList ->
            adapter.submitList(thisList.getSorted())
        }
    }

    private fun List<Note>.getSorted(): List<Note>? {
        return when (sortOption) {
            sortByTitle -> this.sortedBy { note -> note.title }
            sortByTitleDescend -> this.sortedByDescending { note -> note.title }
            sortByTime -> this.sortedBy { note -> note.time }
            sortByTimeDescend -> this.sortedByDescending { note -> note.time }
            else -> null
        }
    }

}