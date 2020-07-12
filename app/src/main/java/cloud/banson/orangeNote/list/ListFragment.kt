package cloud.banson.orangeNote.list

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cloud.banson.orangeNote.R
import cloud.banson.orangeNote.database.Note
import cloud.banson.orangeNote.database.NoteDatabase
import cloud.banson.orangeNote.databinding.FragmentListBinding

class ListFragment : Fragment(), OnItemTouchCallBackListener {
    companion object {
        private const val sortByTitle = 0
        private const val sortByTitleDescend = 1
        private const val sortByTime = 2
        private const val sortByTimeDescend = 3
        private const val sortByCustom = 4
        private const val TAG = "ListFragment"
    }

    private var noteList: MutableList<Note>? = null
    private var sortOption = sortByTimeDescend
    lateinit var adapter: NoteAdapter
    lateinit var myMenu: Menu

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

        adapter = NoteAdapter(NoteListener { id: Long ->
            viewModel.onItemClicked(id)
        })
        binding.noteList.adapter = adapter

        viewModel.noteBook.observe(viewLifecycleOwner, Observer { newList ->
            noteList = if (newList.isNotEmpty()) {
                newList.toList() as MutableList<Note>
            } else {
                mutableListOf<Note>()
            }

            updateNow(noteList, adapter)
        })

        setHasOptionsMenu(true)

        val itemHelperCallback = ItemHelperCallback(this)
        val itemHelper = ItemHelper(itemHelperCallback)
        val recyclerView = binding.noteList
        itemHelper.attachToRecyclerView(recyclerView)

        return binding.root
    }

    private fun updateNow(newList: MutableList<Note>?, adapter: NoteAdapter) {
        newList?.let { thisList ->
            thisList.getSorted()

            adapter.submitList(thisList.toList())

        }
    }

    private fun MutableList<Note>.getSorted() {
        when (sortOption) {
            sortByTitle -> this.sortBy { note -> note.title }
            sortByTitleDescend -> this.sortByDescending { note -> note.title }
            sortByTime -> this.sortBy { note -> note.time }
            sortByTimeDescend -> this.sortByDescending { note -> note.time }
            sortByCustom -> this.sortByDescending { note -> note.importance }
        }
    }

    private fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
        val temp = this[index1]
        this[index1] = this[index2]
        this[index2] = temp
    }

    private fun onCustomSortSelected() {
        val menuItem = myMenu.findItem(R.id.option_sortByCustom)
        menuItem.isChecked = true
        sortOption = sortByCustom
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        myMenu = menu
        inflater.inflate(R.menu.menu_sort, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.option_sortByTimeDescend -> {
                item.isChecked = true
                sortOption = sortByTimeDescend
                updateNow(noteList, adapter)
                true
            }

            R.id.option_sortByTime -> {
                item.isChecked = true
                sortOption = sortByTime
                updateNow(noteList, adapter)
                true
            }

            R.id.option_sortByTitleDescend -> {
                item.isChecked = true
                sortOption = sortByTitleDescend
                updateNow(noteList, adapter)
                true
            }

            R.id.option_sortByTitle -> {
                item.isChecked = true
                sortOption = sortByTitle
                updateNow(noteList, adapter)
                true
            }

            R.id.option_sortByCustom -> {
                onCustomSortSelected()
                updateNow(noteList, adapter)
                true
            }

            else -> super.onOptionsItemSelected(item)

        }
    }

    override fun onMove(sourcePosition: Int, targetPosition: Int): Boolean {

        if (noteList != null) {

            noteList!!.swap(sourcePosition, targetPosition)

            var i = noteList!!.size.toLong()
            for (x in noteList!!) {
                x.importance = i
                i -= 1
            }

            onCustomSortSelected()
            updateNow(noteList, adapter)
            //TODO("Access database by coroutine.")
            return true
        }
        return false
    }

    override fun onSwipe(itemPosition: Int) {
        Log.d("ItemHelper", "onSwipe called")
        noteList?.removeAt(itemPosition)
        updateNow(noteList, adapter)
        //TODO("Access database by coroutine.")
    }
}