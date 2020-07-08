package cloud.banson.orangeNote.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import cloud.banson.orangeNote.list.ListFragmentDirections
import cloud.banson.orangeNote.R
import cloud.banson.orangeNote.database.NoteDatabase
import cloud.banson.orangeNote.databinding.FragmentListBinding

class ListFragment : Fragment() {
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

        val viewModel =
            ViewModelProviders.of(
                this, viewModelFactory
            ).get(ListViewModel::class.java)

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        binding.viewModel.navigateToDetailsFragment.observe(viewLifecycleOwner, Observer {
            val navController = this.findNavController()
            navController.navigate(ListFragmentDirections.actionListFragmentToDetailsFragment())
            viewModel.doneNavigating()
        })

        return binding.root
    }
}